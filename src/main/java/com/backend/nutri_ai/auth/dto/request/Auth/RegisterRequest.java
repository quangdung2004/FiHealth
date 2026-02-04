package com.backend.nutri_ai.auth.dto.request.Auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class  RegisterRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;
}
