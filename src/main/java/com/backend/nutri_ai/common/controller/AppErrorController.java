package com.backend.nutri_ai.common.controller;

import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {

        Object statusObj = request.getAttribute("jakarta.servlet.error.status_code");
        int status = (statusObj instanceof Integer) ? (Integer) statusObj : 500;

        if (status == 404) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail(
                            ErrorCode.ENDPOINT_NOT_FOUND.name(),
                            ErrorCode.ENDPOINT_NOT_FOUND.getMessage()
                    ));
        }

        if (status == 405) {
            return ResponseEntity.status(405)
                    .body(ApiResponse.fail(
                            ErrorCode.METHOD_NOT_ALLOWED.name(),
                            ErrorCode.METHOD_NOT_ALLOWED.getMessage()
                    ));
        }

        // fallback
        return ResponseEntity.status(status)
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_ERROR.name(),
                        ErrorCode.INTERNAL_ERROR.getMessage()
                ));
    }
}
