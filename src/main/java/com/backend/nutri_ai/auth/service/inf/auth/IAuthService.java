package com.backend.nutri_ai.auth.service.inf.auth;

import com.backend.nutri_ai.auth.dto.request.Auth.*;
import com.backend.nutri_ai.auth.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request);

    void register(RegisterRequest request);

    void verifyRegisterOtp(VerifyOtpRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void sendResetPasswordOtp(String email);

    void resetPassword(ResetPasswordRequest request);

    void logout(String refreshToken);
}
