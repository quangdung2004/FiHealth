package com.backend.nutri_ai.auth.controller;



import com.backend.nutri_ai.auth.dto.request.Auth.*;
import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.auth.dto.response.auth.AuthResponse;

import com.backend.nutri_ai.auth.service.inf.auth.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /* ================= LOGIN ================= */

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.login(request))
        );
    }

    /* ================= REGISTER ================= */

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* ================= VERIFY OTP ================= */

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        authService.verifyRegisterOtp(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* ================= REFRESH TOKEN ================= */

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.refreshToken(request))
        );
    }

    /* ================= FORGOT PASSWORD ================= */

    /* ================= FORGOT PASSWORD ================= */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request // <--- Đổi @RequestParam thành @RequestBody
    ) {
        authService.sendResetPasswordOtp(request.getEmail()); // Lấy email từ request object
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* ================= RESET PASSWORD ================= */

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* ================= LOGOUT ================= */

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
