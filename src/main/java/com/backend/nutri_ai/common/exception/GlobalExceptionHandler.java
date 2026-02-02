package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;
import com.backend.nutri_ai.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private String req(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        return method + " " + uri + (qs != null ? "?" + qs : "");
    }

    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BaseBusinessException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus httpStatus = errorCode.getHttpStatus();

        // Log phân biệt 4xx / 5xx
        if (httpStatus.is4xxClientError()) {
            log.warn(
                    "[BUSINESS_ERROR] {} status={} code={} msg={}",
                    req(request),
                    httpStatus.value(),
                    errorCode.name(),
                    ex.getMessage()
            );
        } else {
            log.error(
                    "[BUSINESS_ERROR] {} status={} code={} msg={}",
                    req(request),
                    httpStatus.value(),
                    errorCode.name(),
                    ex.getMessage(),
                    ex
            );
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.fail(
                        errorCode.name(),
                        ex.getMessage()
                ));
    }
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(Exception ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.INVALID_JSON;
        log.warn("[INVALID_JSON] {} msg={}", req(request), ex.getMessage());
        return ResponseEntity.status(ec.getHttpStatus())
                .body(ApiResponse.fail(ec.name(), ec.getMessage()));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgNotValid(Exception ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.INVALID_REQUEST;
        log.warn("[INVALID_REQUEST] {} msg={}", req(request), ex.getMessage());
        return ResponseEntity.status(ec.getHttpStatus())
                .body(ApiResponse.fail(ec.name(), ec.getMessage()));
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(Exception ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.CONSTRAINT_VIOLATION;
        log.warn("[CONSTRAINT] {} msg={}", req(request), ex.getMessage());
        return ResponseEntity.status(ec.getHttpStatus())
                .body(ApiResponse.fail(ec.name(), ec.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;
        log.error("[UNHANDLED] {} msg={}", req(request), ex.getMessage(), ex);
        return ResponseEntity.status(ec.getHttpStatus())
                .body(ApiResponse.fail(ec.name(), ec.getMessage()));
    }

}
