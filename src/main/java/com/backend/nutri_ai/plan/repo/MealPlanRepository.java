package com.backend.nutri_ai.plan.repo;

import com.backend.nutri_ai.plan.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    Optional<MealPlan> findByIdAndUserId(UUID id, UUID userId);
}
