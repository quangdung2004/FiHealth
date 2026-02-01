package com.backend.nutri_ai.payment.entity;


import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.common.enums.PlanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "payment_transaction",
        indexes = @Index(name = "ux_order_code", columnList = "orderCode", unique = true)
)
@Getter @Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(length = 500)
    private String qrCode;

    @Column(length = 500)
    private String checkoutUrl;

    private Instant expiredAt;
    private Instant paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser user;
}
