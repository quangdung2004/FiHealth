package com.backend.nutri_ai.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* =====================================================
     * AUTH / ACCOUNT
     * ===================================================== */
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    USER_BLOCKED(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),
    ACCOUNT_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "Tài khoản chưa được kích hoạt"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email đã tồn tại"),

    /* =====================================================
     * TOKEN / SECURITY
     * ===================================================== */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Chưa xác thực"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không có quyền truy cập"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access token không hợp lệ"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access token đã hết hạn"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token đã hết hạn"),

    /* =====================================================
     * VALIDATION / REQUEST
     * ===================================================== */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "Request body không hợp lệ"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "Tham số không hợp lệ"),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "Thiếu trường bắt buộc"),

    /* =====================================================
     * RESOURCE / 404
     * ===================================================== */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên"),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "API không tồn tại"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy file"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức HTTP không được hỗ trợ"),


    /* =====================================================
     * DUPLICATE / CONFLICT
     * ===================================================== */
    DUPLICATED_RESOURCE(HttpStatus.CONFLICT, "Tài nguyên đã tồn tại"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "Vi phạm ràng buộc dữ liệu"),

    /* =====================================================
     * RATE LIMIT / 429
     * ===================================================== */
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "Quá nhiều yêu cầu, vui lòng thử lại sau"),
    AI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Đã vượt quá giới hạn AI"),
    INVALID_OTP(HttpStatus.BAD_REQUEST, "OTP không hợp lệ"),
    EXPIRED_OTP(HttpStatus.BAD_REQUEST, "OTP đã hết hạn"),
    OTP_TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "Nhập OTP quá nhiều lần"),

    /* =====================================================
     * AI / EXTERNAL SERVICE
     * ===================================================== */
    AI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Dịch vụ AI tạm thời không khả dụng"),
    AI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "AI xử lý quá thời gian"),
    AI_RESPONSE_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "Phản hồi AI không hợp lệ"),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "Lỗi hệ thống bên thứ ba"),
    EXTERNAL_API_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Hệ thống bên thứ ba phản hồi quá chậm"),

    /* =====================================================
     * FILE / STORAGE
     * ===================================================== */
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "Ảnh là bắt buộc"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload file thất bại"),
    UPLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "File vượt quá dung lượng cho phép"),
    STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi lưu trữ file"),

    /* =====================================================
     * DATABASE / TIMEOUT
     * ===================================================== */
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi cơ sở dữ liệu"),
    DATABASE_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối database"),
    DATABASE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Kết nối database quá thời gian"),
    TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xử lý giao dịch"),

    /* =====================================================
     * SYSTEM
     * ===================================================== */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Hệ thống đang bảo trì");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
