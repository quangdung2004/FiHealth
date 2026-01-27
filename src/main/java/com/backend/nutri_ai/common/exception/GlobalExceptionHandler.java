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
}
