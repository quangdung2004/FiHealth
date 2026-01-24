package com.backend.nutri_ai.auth.service.impl.auth;


import com.backend.nutri_ai.auth.config.JwtConfig;
import com.backend.nutri_ai.auth.constant.MailConstant;
import com.backend.nutri_ai.auth.constant.SecurityConstant;
import com.backend.nutri_ai.auth.dto.request.Auth.*;
import com.backend.nutri_ai.auth.dto.response.AuthResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.RefreshToken;
import com.backend.nutri_ai.auth.repository.RefreshTokenRepository;
import com.backend.nutri_ai.auth.repository.UserRepository;
import com.backend.nutri_ai.auth.security.JwtService;
import com.backend.nutri_ai.auth.Exceptions.AppException;
import com.backend.nutri_ai.auth.service.inf.auth.IAuthService;
import com.backend.nutri_ai.auth.util.OtpGenerator;
import com.backend.nutri_ai.auth.constant.ErrorCode;
import com.backend.nutri_ai.auth.constant.RedisKey;
import com.backend.nutri_ai.auth.Mail.service.MailService;
import com.backend.nutri_ai.auth.service.inf.redis.RedisService;
import com.backend.nutri_ai.common.enums.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final MailService mailService;
    private final JwtConfig jwtConfig;
    /* ================= LOGIN ================= */

    public AuthResponse login(LoginRequest request) {

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus().equals(UserStatus.BLOCKED)) {
            throw new AppException(ErrorCode.USER_BLOCKED);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = rotateRefreshToken(user);

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    /* ================= REGISTER ================= */

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.VERIFY);

        userRepository.save(user);

        sendRegisterOtp(user.getEmail());
    }

    /* ================= VERIFY REGISTER OTP ================= */

    public void verifyRegisterOtp(VerifyOtpRequest request) {

        String key = RedisKey.REGISTER_OTP + request.getEmail();
        String cachedOtp = redisService.get(key);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        redisService.delete(key);
    }

    /* ================= REFRESH TOKEN ================= */

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken token = refreshTokenRepository
                .findByTokenAndIsEnableTrue(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (token.getExpireTime().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        token.setIsEnable(false);
        refreshTokenRepository.save(token);

        AppUser user = token.getUser();

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                rotateRefreshToken(user)
        );
    }

    /* ================= FORGOT PASSWORD ================= */

    public void sendResetPasswordOtp(String email) {

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        rateLimitOtp(email);

        String otp = OtpGenerator.generate();

        redisService.set(
                RedisKey.RESET_PASSWORD_OTP + email,
                otp,
                Duration.ofMinutes(MailConstant.OTP_EXPIRE_MINUTES)
        );

        mailService.sendResetPasswordMail(email, otp);
    }

    /* ================= RESET PASSWORD ================= */

    public void resetPassword(ResetPasswordRequest request) {

        String key = RedisKey.RESET_PASSWORD_OTP + request.getEmail();
        String cachedOtp = redisService.get(key);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisService.delete(key);
    }

    /* ================= LOGOUT ================= */

    public void logout(String refreshToken) {

        RefreshToken token = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        token.setIsEnable(false);
        refreshTokenRepository.save(token);
    }

    /* ================= PRIVATE ================= */

    private String rotateRefreshToken(AppUser user) {

        refreshTokenRepository.disableAllByUserId(user.getId());

        String token = jwtService.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setIsEnable(true);
        refreshToken.setExpireTime(
                Instant.now().plusSeconds(SecurityConstant.REFRESH_TOKEN_TTL_SECONDS)
        );

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private void sendRegisterOtp(String email) {

        rateLimitOtp(email);

        String otp = OtpGenerator.generate();

        redisService.set(
                RedisKey.REGISTER_OTP + email,
                otp,
                Duration.ofMinutes(MailConstant.OTP_EXPIRE_MINUTES)
        );

        mailService.sendOtpMail(email, otp);
    }

    private void rateLimitOtp(String email) {

        String key = RedisKey.OTP_RATE_LIMIT + email;

        if (redisService.exists(key)) {
            throw new AppException(ErrorCode.OTP_TOO_MANY_ATTEMPTS);
        }

        redisService.set(
                key,
                "1",
                Duration.ofMinutes(1)
        );
    }
}
