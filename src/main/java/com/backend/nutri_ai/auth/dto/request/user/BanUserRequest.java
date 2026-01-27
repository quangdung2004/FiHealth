package com.backend.nutri_ai.auth.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BanUserRequest {
    @NotBlank(message = "Vui lòng nhập lý do khóa tài khoản")
    private String reason;
}
