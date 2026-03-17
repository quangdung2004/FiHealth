package com.backend.nutri_ai.plan.dto;

import com.backend.nutri_ai.common.enums.MealType;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class MealPlanDto {
    private UUID id;
    private PlanPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean favorite;
    private List<DayDto> days;

    @Data public static class DayDto {
        private int dayIndex;
        private LocalDate date;
        private List<MealDto> meals;
    }

    @Data public static class MealDto {
        private int mealOrder;
        private MealType mealType;
        private String name;
        private List<ItemDto> items;
    }

    @Data public static class ItemDto {
        private UUID foodItemId;
        private String foodItemName;
        private UUID recipeId;
        private String recipeName;
        private String amount;
    }
}
