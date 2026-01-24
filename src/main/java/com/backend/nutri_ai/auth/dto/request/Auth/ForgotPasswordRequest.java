package com.backend.nutri_ai.auth.dto.request.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ForgotPasswordRequest {
    @Email
    @NotBlank
    private String email;
}
