package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import com.backend.nutri_ai.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentTransactionRepository txRepo;

    public PaymentStatus getStatus(String orderCode) {

        PaymentTransaction tx = txRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("PAYMENT_NOT_FOUND"));

        if (tx.getStatus() == PaymentStatus.PENDING
                && tx.getExpiredAt() != null
                && tx.getExpiredAt().isBefore(Instant.now())) {
            return PaymentStatus.EXPIRED;
        }

        return tx.getStatus();
    }
}

