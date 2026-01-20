package com.backend.nutri_ai.admin.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="subscription_plan", indexes = {
        @Index(name="ux_plan_code", columnList="code", unique=true)
})
@Getter @Setter
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable=false, length=40)
    private String code; // FREE, MEDIUM, EXPERT, FAMILY

    @Column(nullable=false, length=120)
    private String name;

    @Column(nullable=false)
    private Integer priceVndPerMonth;

    @Column(nullable=false)
    private Integer aiRequestsPerDay;

    @Column(nullable=false)
    private Integer imageAnalysesPerDay;

    @Column(nullable=false)
    private Integer maxPlanDays; // 1/7/30...

    @Column(nullable=false)
    private Boolean family = false;

    @Column(nullable=false)
    private Boolean active = true;
}
