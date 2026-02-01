package com.backend.nutri_ai.auth.service.impl.auth;


import com.backend.nutri_ai.auth.config.JwtConfig;
import com.backend.nutri_ai.auth.constant.MailConstant;
import com.backend.nutri_ai.auth.constant.SecurityConstant;
import com.backend.nutri_ai.auth.dto.request.Auth.*;
import com.backend.nutri_ai.auth.dto.response.auth.AuthResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.RefreshToken;
import com.backend.nutri_ai.auth.repository.RefreshTokenRepository;
import com.backend.nutri_ai.auth.repository.UserRepository;
import com.backend.nutri_ai.auth.security.JwtService;
import com.backend.nutri_ai.common.exception.*;
import com.backend.nutri_ai.auth.service.inf.auth.IAuthService;
import com.backend.nutri_ai.auth.util.OtpGenerator;
import com.backend.nutri_ai.common.enums.ErrorCode;
import com.backend.nutri_ai.auth.constant.RedisKey;
import com.backend.nutri_ai.auth.Mail.service.MailService;
import com.backend.nutri_ai.auth.service.inf.redis.RedisService;
import com.backend.nutri_ai.auth.util.generateRandomPassword;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.common.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.authentication.core.TokenRequestException;
import org.springframework.security.core.Authentication;


import javax.security.auth.RefreshFailedException;
import javax.security.auth.login.CredentialException;
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
    private final MailService mailService;
    private final JwtConfig jwtConfig;
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
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.VERIFY);

        userRepository.save(user);

        sendRegisterOtp(user.getEmail());
    }

    /* ================= VERIFY REGISTER OTP ================= */

    public void verifyRegisterOtp(VerifyOtpRequest request) {

        String key = RedisKey.REGISTER_OTP + request.getEmail();
        String cachedOtp = redisService.get(key);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        redisService.delete(key);
    }

    /* ================= REFRESH TOKEN ================= */

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken token = refreshTokenRepository
                .findByTokenAndIsEnableTrue(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.getExpireTime().isBefore(Instant.now())) {
            throw new ExpiredRefreshTokenException("Token has expired");
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
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

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

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Kiểm tra OTP từ Redis
        String key = RedisKey.RESET_PASSWORD_OTP + request.getEmail();
        String cachedOtp = redisService.get(key);

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // 2. Lấy User từ DB
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // 3. Sử dụng Util để sinh mật khẩu ngẫu nhiên (Ví dụ: A8kMz9Lp2q)
        String newRandomPassword = generateRandomPassword.generate();

        // 4. Mã hóa và lưu mật khẩu mới vào Database
        user.setPasswordHash(passwordEncoder.encode(newRandomPassword));
        userRepository.save(user);

        // 5. Gửi email chứa mật khẩu mới cho user
        mailService.sendNewPasswordMail(user.getEmail(), newRandomPassword);

        // 6. Xóa OTP sau khi hoàn tất
        redisService.delete(key);
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

        Object principal = auth.getPrincipal(); // bạn set principal = user.getId() (UUID)
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
