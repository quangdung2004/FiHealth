package com.backend.nutri_ai.payment.repository;

import com.backend.nutri_ai.common.enums.PlanType;
import com.backend.nutri_ai.payment.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByPlanTypeAndActiveTrue(PlanType planType);
    boolean existsByPlanType(PlanType planType);
}
