package com.backend.nutri_ai.plan.mapper;

import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.plan.dto.MealPlanDetailResponse;
import com.backend.nutri_ai.plan.entity.MealItem;
import com.backend.nutri_ai.plan.entity.MealPlan;
import com.backend.nutri_ai.plan.entity.PlanDay;
import com.backend.nutri_ai.plan.entity.PlanMeal;
import com.backend.nutri_ai.catalog.entity.RecipeIngredient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MealPlanDetailMapper {

    private MealPlanDetailMapper() {}

    public static MealPlanDetailResponse toDto(MealPlan plan, Map<UUID, Recipe> recipeMap) {
        if (plan == null) {
            return null;
        }

        // ✅ Quan trọng: khai báo generic rõ ràng để không bị List<Object>
        List<MealPlanDetailResponse.DayDto> days =
                plan.getDays() == null ? List.of() :
                        plan.getDays().stream()
                                .sorted(Comparator.comparingInt(PlanDay::getDayIndex))
                                .map(d -> new MealPlanDetailResponse.DayDto(
                                        d.getDayIndex(),
                                        d.getDate(),
                                        nullToZero(d.getTotalKcal()),
                                        nullToZero(d.getCostVnd()),
                                        mapMeals(d, recipeMap)
                                ))
                                .toList();

        return new MealPlanDetailResponse(
                plan.getId(),
                plan.getPeriod(),
                plan.getStartDate(),
                plan.getEndDate(),
                nullToZero(plan.getTotalDays()),
                nullToZero(plan.getEstimatedTotalCostVnd()),
                days
        );
    }

    private static List<MealPlanDetailResponse.MealDto> mapMeals(PlanDay d, Map<UUID, Recipe> recipeMap) {
        if (d.getMeals() == null) return List.of();

        return d.getMeals().stream()
                .sorted(Comparator.comparingInt(PlanMeal::getMealOrder))
                .map(m -> new MealPlanDetailResponse.MealDto(
                        m.getMealOrder(),
                        m.getName(),
                        m.getMealType(),
                        nullToZero(m.getKcal()),
                        nullToZero(m.getCostVnd()),
                        mapItems(m, recipeMap)
                ))
                .toList();
    }

    private static List<MealPlanDetailResponse.ItemDto> mapItems(PlanMeal m, Map<UUID, Recipe> recipeMap) {
        if (m.getItems() == null) return List.of();

        return m.getItems().stream()
                .map(it -> toItemDto(it, recipeMap))
                .toList();
    }

    private static MealPlanDetailResponse.ItemDto toItemDto(MealItem it, Map<UUID, Recipe> recipeMap) {
        if (it == null) {
            return new MealPlanDetailResponse.ItemDto(
                    null,
                    "Unknown",
                    "",
                    0,
                    0,
                    List.of()
            );
        }

        // Nếu không có recipe -> trả snapshot foodName
        if (it.getRecipe() == null) {
            return new MealPlanDetailResponse.ItemDto(
                    null,
                    safeStr(it.getFoodName(), "Unknown"),
                    safeStr(it.getAmount(), ""),
                    nullToZero(it.getKcal()),
                    nullToZero(it.getCostVnd()),
                    List.of()
            );
        }

        UUID rid = it.getRecipe().getId();
        Recipe r = (rid != null && recipeMap != null) ? recipeMap.get(rid) : null;

        // Nếu recipe không load được -> vẫn trả snapshot
        if (r == null) {
            return new MealPlanDetailResponse.ItemDto(
                    rid,
                    safeStr(it.getFoodName(), "Unknown"),
                    safeStr(it.getAmount(), ""),
                    nullToZero(it.getKcal()),
                    nullToZero(it.getCostVnd()),
                    List.of()
            );
        }

        List<MealPlanDetailResponse.IngredientDto> ingredients =
                r.getIngredients() == null ? List.of() :
                        r.getIngredients().stream()
                                .map(ing -> toIngredientDto(ing))
                                .toList();

        return new MealPlanDetailResponse.ItemDto(
                r.getId(),
                r.getName(),
                safeStr(it.getAmount(), ""),
                nullToZero(it.getKcal()),
                nullToZero(it.getCostVnd()),
                ingredients
        );
    }

    private static MealPlanDetailResponse.IngredientDto toIngredientDto(RecipeIngredient ing) {
        if (ing == null || ing.getFoodItem() == null) {
            return new MealPlanDetailResponse.IngredientDto(null, "Unknown", "");
        }
        return new MealPlanDetailResponse.IngredientDto(
                ing.getFoodItem().getId(),
                ing.getFoodItem().getName(),
                safeStr(ing.getAmount(), "")
        );
    }

    private static int nullToZero(Integer v) {
        return v == null ? 0 : v;
    }

    private static String safeStr(String v, String fallback) {
        return (v == null || v.isBlank()) ? fallback : v;
    }
}
