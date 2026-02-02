package com.backend.nutri_ai.payment.repository;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select tx
        from PaymentTransaction tx
        join fetch tx.user
        where tx.orderCode = :orderCode
    """)
    Optional<PaymentTransaction> findByOrderCodeForUpdate(@Param("orderCode") String orderCode);
}
