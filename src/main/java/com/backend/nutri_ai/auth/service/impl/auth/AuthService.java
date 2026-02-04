package com.backend.nutri_ai.auth.service.impl.auth;

import com.backend.nutri_ai.auth.config.JwtConfig;
import com.backend.nutri_ai.auth.constant.MailConstant;
import com.backend.nutri_ai.auth.constant.RedisKey;
import com.backend.nutri_ai.auth.constant.SecurityConstant;
import com.backend.nutri_ai.auth.dto.PendingRegisterUser;
import com.backend.nutri_ai.auth.dto.request.Auth.*;
import com.backend.nutri_ai.auth.dto.response.auth.AuthResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.RefreshToken;
import com.backend.nutri_ai.auth.repository.RefreshTokenRepository;
import com.backend.nutri_ai.auth.repository.UserRepository;
import com.backend.nutri_ai.auth.security.JwtService;
import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.auth.service.inf.auth.IAuthService;
import com.backend.nutri_ai.auth.service.inf.redis.RedisService;
import com.backend.nutri_ai.auth.util.OtpGenerator;
import com.backend.nutri_ai.common.enums.ErrorCode;
import com.backend.nutri_ai.common.enums.UserEventType;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.common.exception.*;
import com.backend.nutri_ai.common.exception.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final com.backend.nutri_ai.auth.Mail.service.MailService mailService;
    private final JwtConfig jwtConfig;
    private final UserEventService eventService;
    private final ObjectMapper objectMapper;

    /* ================= LOGIN ================= */
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UserNotFoundException("Password wrong");
        }

        if (user.getStatus().equals(UserStatus.BLOCKED)) {
            throw new UserNotFoundException("User blocked");
        }

        // (optional) nếu bạn vẫn muốn chặn VERIFY trong DB
        if (user.getStatus().equals(UserStatus.VERIFY)) {
            throw new UnauthorizedException("Tài khoản chưa được xác thực");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = rotateRefreshToken(user);

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        eventService.track(UserEventType.AUTH_LOGIN_SUCCESS, user.getId(), true,
                "WEB", null, null, null);

        return new AuthResponse(accessToken, refreshToken);
    }

    /* ================= REGISTER ================= */

    /* ================= REGISTER ================= */
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        rateLimitOtp(request.getEmail());

        String otp = OtpGenerator.generate();
        redisService.set(
                RedisKey.REGISTER_OTP + request.getEmail(),
                otp,
                Duration.ofMinutes(MailConstant.OTP_EXPIRE_MINUTES)
        );

        String passwordHash = passwordEncoder.encode(request.getPassword());

        PendingRegisterUser pending = PendingRegisterUser.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordHash)
                .role(UserRole.USER.name())
                .build();

        try {
            String pendingKey = RedisKey.REGISTER_PENDING + request.getEmail();
            redisService.set(
                    pendingKey,
                    objectMapper.writeValueAsString(pending),
                    Duration.ofMinutes(MailConstant.OTP_EXPIRE_MINUTES)
            );
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.DATABASE_ERROR);
        }

        mailService.sendOtpMail(request.getEmail(), otp);
    }

    /* ================= VERIFY REGISTER OTP ================= */

    public void verifyRegisterOtp(VerifyOtpRequest request) {

        String otpKey = RedisKey.REGISTER_OTP + request.getEmail();
        String cachedOtp = redisService.get(otpKey);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        String pendingKey = RedisKey.REGISTER_PENDING + request.getEmail();
        String pendingJson = redisService.get(pendingKey);

        if (pendingJson == null) {
            // OTP đúng nhưng pending info hết hạn / bị xoá
            throw new InvalidOtpException("Pending register expired");
        }

        PendingRegisterUser pending;
        try {
            pending = objectMapper.readValue(pendingJson, PendingRegisterUser.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.DATABASE_ERROR);
        }

        // ✅ Tạo user thật trong DB
        AppUser user = new AppUser();
        user.setEmail(pending.getEmail());
        user.setFullName(pending.getFullName());
        user.setPasswordHash(pending.getPasswordHash());
        user.setRole(UserRole.valueOf(pending.getRole()));
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        // cleanup redis
        redisService.delete(otpKey);
        redisService.delete(pendingKey);

        eventService.track(UserEventType.AUTH_REGISTER_VERIFY_SUCCESS, user.getId(), true,
                "WEB", null, null, null);
    }

    /* ================= REFRESH TOKEN ================= */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository
                .findByTokenAndIsEnableTrue(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.getExpireTime().isBefore(Instant.now())) {
            eventService.track(UserEventType.AUTH_REFRESH_TOKEN_FAILED, token.getUser().getId(), false,
                    "WEB", null, "{\"reason\":\"expired\"}", null);
            throw new ExpiredRefreshTokenException("Token has expired");
        }

        token.setIsEnable(false);
        refreshTokenRepository.save(token);

        AppUser user = token.getUser();
        eventService.track(UserEventType.AUTH_REFRESH_TOKEN_SUCCESS, user.getId(), true,
                "WEB", null, null, null);

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                rotateRefreshToken(user)
        );
    }

    /* ================= FORGOT PASSWORD ================= */
    public void sendResetPasswordOtp(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        rateLimitOtp(email);

        String otp = OtpGenerator.generate();
        redisService.set(
                RedisKey.RESET_PASSWORD_OTP + email,
                otp,
                Duration.ofMinutes(MailConstant.OTP_EXPIRE_MINUTES)
        );

        mailService.sendResetPasswordMail(email, otp);
        eventService.track(UserEventType.AUTH_RESET_OTP_SENT, user.getId(), true,
                "WEB", null, null, null);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String key = RedisKey.RESET_PASSWORD_OTP + request.getEmail();
        String cachedOtp = redisService.get(key);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        String newRandomPassword = com.backend.nutri_ai.auth.util.generateRandomPassword.generate();

        user.setPasswordHash(passwordEncoder.encode(newRandomPassword));
        userRepository.save(user);

        mailService.sendNewPasswordMail(user.getEmail(), newRandomPassword);

        redisService.delete(key);
        eventService.track(UserEventType.AUTH_RESET_PASSWORD_SUCCESS, user.getId(), true,
                "WEB", null, null, null);
    }

    /* ================= LOGOUT ================= */
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

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

    private void rateLimitOtp(String email) {
        String key = RedisKey.OTP_RATE_LIMIT + email;

        if (redisService.exists(key)) {
            throw new OtpTooManyAttemptsException("OtpTooManyAttemptsException");
        }

        redisService.set(
                key,
                "1",
                Duration.ofMinutes(1)
        );
    }

    public AppUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }

        Object principal = auth.getPrincipal();
        UUID userId;
        try {
            if (principal instanceof UUID id) userId = id;
            else userId = UUID.fromString(String.valueOf(principal));
        } catch (Exception e) {
            throw new UnauthorizedException("Token không hợp lệ");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User không tồn tại"));
    }
}
