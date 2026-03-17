package com.backend.nutri_ai.plan.repo;

import com.backend.nutri_ai.plan.entity.UserFavoriteMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FavoriteMealPlanRepository extends JpaRepository<UserFavoriteMealPlan, UUID> {
    Optional<UserFavoriteMealPlan> findByUserIdAndMealPlanId(UUID userId, UUID mealPlanId);
}
