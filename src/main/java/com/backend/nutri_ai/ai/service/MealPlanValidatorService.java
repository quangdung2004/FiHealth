package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanValidatorService {

    public record DayTotals(int kcal, int costVnd) {}

    // 1 bữa = 3 món
    private static final int ITEMS_PER_MEAL = 3;

    // clamp servings
    private static final double MIN_SERVINGS = 0.5;
    private static final double MAX_SERVINGS = 2.0;

    // budget tolerance
    private static final double MIN_BUDGET_RATIO = 0.90; // >= 90%
    private static final double MAX_BUDGET_RATIO = 1.00; // <= 100%

    public Map<Integer, DayTotals> validateAndComputeTotals(
            NutritionAssessment assessment,
            BodyMetricsSnapshot metrics,
            PlanPeriod requestedPeriod,
            List<CandidateDto> candidates,
            MealPlanAiOutput aiOut
    ) {
        if (aiOut == null || aiOut.days() == null || aiOut.days().isEmpty())
            throw bad("AI output trống");
        if (candidates == null || candidates.isEmpty())
            throw bad("Danh sách candidates trống");
        if (assessment == null || metrics == null)
            throw bad("Thiếu assessment/metrics");

        int expectedDays = expectedDays(requestedPeriod);
        int mealsPerDay = assessment.getMealsPerDay();
        if (mealsPerDay <= 0) throw bad("mealsPerDay không hợp lệ");

        int budget = assessment.getBudgetPerDayVnd() != null ? assessment.getBudgetPerDayVnd() : 0;
        int targetKcal = metrics.getCalorieTarget();
        if (targetKcal <= 0) throw bad("calorieTarget không hợp lệ");

        int tolerance = Math.max(100, (int) Math.round(targetKcal * 0.10));

        // build candidate map
        Map<String, CandidateDto> candMap = new HashMap<>();
        for (CandidateDto c : candidates) {
            validateCandidateDto(c);
            candMap.putIfAbsent(c.id(), c);
        }

        Map<Integer, DayTotals> totalsByDay = new LinkedHashMap<>();
        Set<Integer> seenDayIndex = new HashSet<>();

        for (MealPlanAiOutput.DayOutput day : aiOut.days()) {
            int dayIndex = day.dayIndex();
            if (dayIndex <= 0 || dayIndex > expectedDays)
                throw bad("dayIndex không hợp lệ: " + dayIndex);
            if (!seenDayIndex.add(dayIndex))
                throw bad("Trùng dayIndex: " + dayIndex);

            int kcal = 0;
            int cost = 0;

            // ===== PASS 1: tính raw =====
            for (MealPlanAiOutput.MealOutput meal : day.meals()) {
                if (meal.items().size() > ITEMS_PER_MEAL)
                    throw bad("Mỗi bữa không được vượt quá " + ITEMS_PER_MEAL + " món");

                Set<String> usedInMeal = new HashSet<>();

                for (MealPlanAiOutput.ItemOutput item : meal.items()) {
                    String cid = item.recipeCandidateId();
                    if (!usedInMeal.add(cid))
                        throw bad("Trùng recipe trong 1 bữa: " + cid);

                    CandidateDto cand = candMap.get(cid);
                    double s = clamp(item.servings(), MIN_SERVINGS, MAX_SERVINGS);

                    kcal += (int) Math.round(cand.kcal() * s);
                    cost += (int) Math.round(cand.costVnd() * s);
                }
            }

            // ===== PASS 2: nếu vượt budget → scale xuống =====
            if (budget > 0 && cost > budget) {
                double ratioDown = (double) budget / cost;
                kcal = (int) Math.round(kcal * ratioDown);
                cost = (int) Math.round(cost * ratioDown);
            }

            // ===== PASS 3: nếu quá rẻ → scale lên đến 90% budget =====
            if (budget > 0) {
                int minBudget = (int) Math.round(budget * MIN_BUDGET_RATIO);

                if (cost < minBudget && cost > 0) {
                    double ratioUp = (double) minBudget / cost;
                    ratioUp = Math.min(ratioUp, MAX_SERVINGS / MIN_SERVINGS);

                    kcal = (int) Math.round(kcal * ratioUp);
                    cost = (int) Math.round(cost * ratioUp);

                    if (cost > budget) cost = budget;
                }
            }

            // ===== LOG nếu vẫn lệch =====
            if (budget > 0) {
                int min = (int) (budget * 0.9);
                if (cost < min || cost > budget) {
                    log.warn("Budget lệch: day={}, cost={}, budget={}", dayIndex, cost, budget);
                }
            }

            if (Math.abs(kcal - targetKcal) > tolerance) {
                log.warn("Kcal lệch: day={}, kcal={}, target={}", dayIndex, kcal, targetKcal);
            }

            totalsByDay.put(dayIndex, new DayTotals(kcal, cost));
        }

        return totalsByDay;
    }

    // ================= helpers =================

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private void validateCandidateDto(CandidateDto c) {
        if (c == null || c.id() == null) throw bad("Candidate null");
        if (!c.id().startsWith("R_")) throw bad("Chỉ cho phép RECIPE: " + c.id());
        if (c.kcal() <= 0) throw bad("kcal phải > 0");
        if (c.costVnd() < 0) throw bad("costVnd không hợp lệ");
    }

    private int expectedDays(PlanPeriod period) {
        return switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
        };
    }

    private BadRequestException bad(String msg) {
        return new BadRequestException(msg);
    }
}
