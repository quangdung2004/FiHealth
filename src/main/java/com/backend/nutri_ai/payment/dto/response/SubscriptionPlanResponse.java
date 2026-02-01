package com.backend.nutri_ai.payment.dto.response;

import com.backend.nutri_ai.common.enums.PlanType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {

    private Long id;
    private PlanType planType;
    private String name;
    private Long price;
    private Integer durationDays;
    private boolean active;
}
