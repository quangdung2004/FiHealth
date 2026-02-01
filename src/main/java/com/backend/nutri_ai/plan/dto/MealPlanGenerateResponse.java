package com.backend.nutri_ai.plan.dto;

import java.util.UUID;

public record MealPlanGenerateResponse(
        UUID mealPlanId,
        int totalDays,
        int estimatedTotalCostVnd
) {}
