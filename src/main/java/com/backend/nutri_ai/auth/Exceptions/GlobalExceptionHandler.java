package com.backend.nutri_ai.auth.Exceptions;

import com.backend.nutri_ai.auth.Exceptions.AppException;
import com.backend.nutri_ai.auth.constant.ErrorCode;
import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================= BUSINESS ERROR ================= */

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {

        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus httpStatus = errorCode.getHttpStatus();

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.error(
                        httpStatus.value(),
                        errorCode.name()
                ));
    }

    /* ================= VALIDATION ERROR ================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        400,
                        "INVALID_REQUEST",
                        ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                                .toList()
                ));
    }

    /* ================= SECURITY ================= */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied() {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        403,
                        "FORBIDDEN"
                ));
    }

    /* ================= SYSTEM ERROR ================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {

        ex.printStackTrace(); // dev log

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        500,
                        ErrorCode.INTERNAL_ERROR.name()
                ));
    }
}
