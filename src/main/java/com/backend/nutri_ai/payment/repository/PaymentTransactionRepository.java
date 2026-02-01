package com.backend.nutri_ai.payment.repository;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByOrderCode(String orderCode);

    List<PaymentTransaction> findAllByStatusAndExpiredAtBefore(
            PaymentStatus status,
            Instant time
    );
}
