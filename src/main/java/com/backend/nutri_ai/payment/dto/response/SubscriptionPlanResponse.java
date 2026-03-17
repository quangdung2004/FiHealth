package com.backend.nutri_ai.payment.dto.response;

import com.backend.nutri_ai.common.enums.PlanType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {

    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;
    private String name;
    private String description;
    private Long price;
    private Integer durationDays;
    private boolean active;
}
