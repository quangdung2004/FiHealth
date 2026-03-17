package com.backend.nutri_ai.auth.dto.request.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String otp;
}
