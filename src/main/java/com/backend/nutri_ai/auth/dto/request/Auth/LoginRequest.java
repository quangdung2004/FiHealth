package com.backend.nutri_ai.auth.dto.request.Auth;

import lombok.Getter;
import lombok.Setter;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
public class LoginRequest {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    private String password;
}
