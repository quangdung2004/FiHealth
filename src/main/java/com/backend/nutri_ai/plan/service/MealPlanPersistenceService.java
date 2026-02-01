package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.common.enums.MealType;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.plan.entity.*;
import com.backend.nutri_ai.plan.repo.MealPlanRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanPersistenceService {

    private final MealPlanRepository mealPlanRepository;
    private final EntityManager em;

    @Transactional
    public MealPlan persist(
            AppUser user,
            NutritionAssessment assessment,
            PlanPeriod period,
            List<CandidateDto> candidates,
            MealPlanAiOutput aiOut,
            Map<Integer, com.backend.nutri_ai.ai.service.MealPlanValidatorService.DayTotals> totalsByDay
    ) {

        Map<String, CandidateDto> candMap = new HashMap<>();
        for (CandidateDto c : candidates) candMap.put(c.id(), c);

        LocalDate start = LocalDate.now();
        int totalDays = switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
        };
        LocalDate end = start.plusDays(totalDays - 1);

        MealPlan plan = new MealPlan();
        plan.setUser(user);
        plan.setAssessment(assessment);
        plan.setPeriod(period);
        plan.setStartDate(start);
        plan.setEndDate(end);
        plan.setTotalDays(totalDays);
        plan.setBudgetPerDayVnd(assessment.getBudgetPerDayVnd() != null ? assessment.getBudgetPerDayVnd() : 0);

        int totalCost = 0;

        List<MealPlanAiOutput.DayOutput> days = new ArrayList<>(aiOut.days());
        days.sort(Comparator.comparingInt(MealPlanAiOutput.DayOutput::dayIndex));

        for (MealPlanAiOutput.DayOutput d : days) {
            int dayIndex = d.dayIndex();
            LocalDate date = start.plusDays(dayIndex - 1);

            PlanDay planDay = new PlanDay();
            planDay.setPlan(plan);
            planDay.setDayIndex(dayIndex);
            planDay.setDate(date);

            var totals = totalsByDay.get(dayIndex);
            planDay.setTotalKcal(totals != null ? totals.kcal() : 0);
            planDay.setCostVnd(totals != null ? totals.costVnd() : 0);
            totalCost += planDay.getCostVnd();

            List<MealPlanAiOutput.MealOutput> meals = new ArrayList<>(d.meals());
            meals.sort(Comparator.comparingInt(MealPlanAiOutput.MealOutput::mealOrder));

            for (MealPlanAiOutput.MealOutput m : meals) {
                PlanMeal planMeal = new PlanMeal();
                planMeal.setDay(planDay);
                planMeal.setMealOrder(m.mealOrder());
                planMeal.setName(m.name() != null ? m.name() : defaultMealName(m.mealOrder()));
                planMeal.setMealType(inferMealType(m.mealOrder(), planMeal.getName()));

                if (m.items() != null) {
                    for (MealPlanAiOutput.ItemOutput it : m.items()) {
                        CandidateDto cand = candMap.get(it.candidateId());
                        if (cand == null) continue;

                        MealItem item = new MealItem();
                        item.setMeal(planMeal);

                        UUID refId = extractUuidFromCandidateId(cand.id());

                        if ("RECIPE".equalsIgnoreCase(cand.type())) {
                            item.setRecipe(em.getReference(Recipe.class, refId));
                            item.setFoodItem(null);
                        } else {
                            item.setFoodItem(em.getReference(FoodItem.class, refId));
                            item.setRecipe(null);
                        }

                        item.setAmount(formatAmount(it.servings(), cand.servingUnit()));
                        planMeal.getItems().add(item);
                    }
                }

                planDay.getMeals().add(planMeal);
            }

            plan.getDays().add(planDay);
        }

        plan.setEstimatedTotalCostVnd(totalCost);
        return mealPlanRepository.save(plan);
    }

    private UUID extractUuidFromCandidateId(String candidateId) {
        // candidateId format: "R_<uuid>" or "F_<uuid>"
        if (candidateId == null || candidateId.length() < 3) {
            throw new IllegalArgumentException("Invalid candidateId: " + candidateId);
        }
        return UUID.fromString(candidateId.substring(2));
    }

    private String formatAmount(double servings, String unit) {
        String s = (servings == (long) servings) ? String.valueOf((long) servings) : String.valueOf(servings);
        if (unit == null || unit.isBlank()) unit = "serving";
        return s + " x " + unit;
    }

    private MealType inferMealType(int order, String name) {
        String n = (name == null) ? "" : name.toLowerCase(Locale.ROOT);
        if (n.contains("sáng")) return MealType.BREAKFAST;
        if (n.contains("trưa")) return MealType.LUNCH;
        if (n.contains("tối")) return MealType.DINNER;
        if (n.contains("phụ") || n.contains("snack")) return MealType.SNACK;

        return switch (order) {
            case 1 -> MealType.BREAKFAST;
            case 2 -> MealType.LUNCH;
            case 3 -> MealType.DINNER;
            default -> MealType.SNACK;
        };
    }

    private String defaultMealName(int order) {
        return switch (order) {
            case 1 -> "Bữa sáng";
            case 2 -> "Bữa trưa";
            case 3 -> "Bữa tối";
            default -> "Bữa phụ";
        };
    }
}
