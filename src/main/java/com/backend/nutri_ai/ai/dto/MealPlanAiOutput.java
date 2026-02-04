package com.backend.nutri_ai.ai.dto;

import com.backend.nutri_ai.common.enums.PlanPeriod;

import java.util.List;

public record MealPlanAiOutput(
        PlanPeriod period,
        List<DayOutput> days,
        String notes
) {
    public record DayOutput(
            int dayIndex,
            List<MealOutput> meals
    ) {}

    public record MealOutput(
            int mealOrder,
            String name,
            List<ItemOutput> items
    ) {}

    // ✅ chỉ cho phép recipe
    public record ItemOutput(
            String recipeCandidateId, // ví dụ: "R_<uuid>"
            Double servings
    ) {}
}
