package com.backend.nutri_ai.payment.entity;

import com.backend.nutri_ai.common.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "PaymentSubscriptionPlan")
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private PlanType planType;

    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Long price;
    private Integer durationDays;
    private boolean active = true;
}
