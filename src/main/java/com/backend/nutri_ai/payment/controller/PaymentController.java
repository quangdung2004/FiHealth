package com.backend.nutri_ai.payment.controller;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.payment.dto.request.CreateQrPaymentRequest;
import com.backend.nutri_ai.payment.dto.response.QrPaymentResponse;
import com.backend.nutri_ai.payment.service.PayOsPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PayOsPaymentService service;

    @PostMapping("/qr")
    public QrPaymentResponse create(
            @RequestBody CreateQrPaymentRequest req
    ) {
        return service.createQr(req.getPlanType());
    }
}

