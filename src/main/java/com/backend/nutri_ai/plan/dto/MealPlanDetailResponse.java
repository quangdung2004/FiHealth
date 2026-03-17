package com.backend.nutri_ai.plan.dto;

import com.backend.nutri_ai.common.enums.MealType;
import com.backend.nutri_ai.common.enums.PlanPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MealPlanDetailResponse(
        UUID id,
        PlanPeriod period,
        LocalDate startDate,
        LocalDate endDate,
        int totalDays,
        int estimatedTotalCostVnd,
        List<DayDto> days
) {
    public record DayDto(
            int dayIndex,
            LocalDate date,
            int totalKcal,
            int costVnd,
            List<MealDto> meals
    ) {}

    public record MealDto(
            int mealOrder,
            String name,
            MealType mealType,
            int kcal,
            int costVnd,
            List<ItemDto> items
    ) {}

    public record ItemDto(
            UUID recipeId,
            String recipeName,
            String amount,
            int kcal,
            int costVnd,
            List<IngredientDto> ingredients
    ) {}

    public record IngredientDto(
            UUID foodItemId,
            String foodItemName,
            String amount
    ) {}
}
