package com.backend.nutri_ai.payment.controller;

import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.payment.dto.request.CreateQrPaymentRequest;
import com.backend.nutri_ai.payment.dto.response.QrPaymentResponse;
import com.backend.nutri_ai.payment.service.PayOsPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PayOsPaymentService service;

    @PostMapping("/qr")
    public ResponseEntity<ApiResponse<QrPaymentResponse>> create(
            @RequestBody CreateQrPaymentRequest req
    ) {
        // Gọi service tạo mã QR từ PayOS
        QrPaymentResponse response = service.createQr(req.getPlanType());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}