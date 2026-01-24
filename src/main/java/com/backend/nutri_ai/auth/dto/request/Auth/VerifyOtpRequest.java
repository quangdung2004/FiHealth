package com.backend.nutri_ai.auth.dto.request.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String otp;
}

