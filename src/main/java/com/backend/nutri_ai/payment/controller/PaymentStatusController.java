package com.backend.nutri_ai.payment.controller;


import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.service.PaymentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentStatusController {

    private final PaymentStatusService statusService;

    @GetMapping("/status/{orderCode}")
    public ResponseEntity<ApiResponse<String>> checkStatus(
            @PathVariable String orderCode
    ) {
        PaymentStatus status = statusService.getStatus(orderCode);
        return ResponseEntity.ok(ApiResponse.success(status.name()));
    }
}
