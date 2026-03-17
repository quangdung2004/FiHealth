package com.backend.nutri_ai.payment.util;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentExpireJob {

    private final PaymentTransactionRepository txRepo;

    @Scheduled(fixedRate = 60_000)
    public void expirePayment() {
        txRepo.findAllByStatusAndExpiredAtBefore(
                PaymentStatus.PENDING,
                Instant.now()
        ).forEach(tx -> tx.setStatus(PaymentStatus.EXPIRED));
    }
}
