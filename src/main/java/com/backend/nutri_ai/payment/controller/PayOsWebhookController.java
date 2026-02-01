package com.backend.nutri_ai.payment.controller;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.config.PayOsConfig;
import com.backend.nutri_ai.payment.dto.request.PayOsWebhookRequest;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import com.backend.nutri_ai.payment.repository.PaymentTransactionRepository;
import com.backend.nutri_ai.payment.service.MembershipService;
import com.backend.nutri_ai.payment.util.PayOsSignatureUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/payment/webhook/payos")
@RequiredArgsConstructor
public class PayOsWebhookController {

    private final PaymentTransactionRepository txRepo;
    private final MembershipService membershipService;
    private final PayOsConfig config;

    @PostMapping
    @Transactional
    public ResponseEntity<String> webhook(
            @RequestBody PayOsWebhookRequest req
    ) {
        String expected = PayOsSignatureUtil.webhookSignature(
                req, config.getChecksumKey()
        );

        if (!expected.equals(req.getSignature()))
            return ResponseEntity.status(403).body("INVALID");

        PaymentTransaction tx = txRepo
                .findByOrderCode(req.getOrderCode())
                .orElse(null);

        if (tx == null || tx.getStatus() != PaymentStatus.PENDING)
            return ResponseEntity.ok("IGNORED");

        if ("PAID".equals(req.getStatus())) {
            tx.setStatus(PaymentStatus.SUCCESS);
            tx.setPaidAt(Instant.now());

            membershipService.upgrade(
                    tx.getUser(),
                    tx.getDurationDays()
            );
            return ResponseEntity.ok("SUCCESS");
        }

        tx.setStatus(PaymentStatus.EXPIRED);
        return ResponseEntity.ok("FAILED");
    }
}

