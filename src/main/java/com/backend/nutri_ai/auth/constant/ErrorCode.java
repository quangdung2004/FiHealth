package com.backend.nutri_ai.auth.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== AUTH =====
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"),
    USER_BLOCKED(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),

    // ===== TOKEN =====
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access token không hợp lệ"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access token đã hết hạn"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token đã hết hạn"),

    // ===== OTP =====
    INVALID_OTP(HttpStatus.BAD_REQUEST, "OTP không hợp lệ"),
    EXPIRED_OTP(HttpStatus.BAD_REQUEST, "OTP đã hết hạn"),
    OTP_TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "Nhập OTP quá nhiều lần"),

    // ===== REGISTER / RESET =====
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email đã tồn tại"),
    ACCOUNT_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "Tài khoản chưa được kích hoạt"),

    // ===== SYSTEM =====
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}