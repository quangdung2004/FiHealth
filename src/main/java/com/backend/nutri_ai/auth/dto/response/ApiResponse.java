package com.backend.nutri_ai.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String status;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "SUCCESS", null);
    }
    public static ApiResponse<Void> error(int code, String status) {
        return new ApiResponse<>(code, status, null);
    }
}
