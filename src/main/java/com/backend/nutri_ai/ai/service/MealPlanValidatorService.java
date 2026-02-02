package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanValidatorService {

    public record DayTotals(int kcal, int costVnd) {}

    // Clamp servings mỗi item
    private static final double MIN_SERVINGS_PER_ITEM = 0.5;
    private static final double MAX_SERVINGS_PER_ITEM = 6.0;

    // Clamp ratio scale toàn ngày
    private static final double MIN_DAY_SCALE_RATIO = 0.5;
    private static final double MAX_DAY_SCALE_RATIO = 3.0;

    public Map<Integer, DayTotals> validateAndComputeTotals(
            NutritionAssessment assessment,
            BodyMetricsSnapshot metrics,
            PlanPeriod requestedPeriod,
            List<CandidateDto> candidates,
            MealPlanAiOutput aiOut
    ) {
        if (aiOut == null || aiOut.days() == null || aiOut.days().isEmpty()) {
            throw bad("AI output trống");
        }
        if (candidates == null || candidates.isEmpty()) {
            throw bad("Danh sách candidates trống");
        }
        if (assessment == null || metrics == null) {
            throw bad("Thiếu assessment/metrics");
        }

        int expectedDays = expectedDays(requestedPeriod);
        int mealsPerDay = assessment.getMealsPerDay();
        if (mealsPerDay <= 0) throw bad("mealsPerDay không hợp lệ");

        int budget = (assessment.getBudgetPerDayVnd() != null ? assessment.getBudgetPerDayVnd() : 0);
        int targetKcal = metrics.getCalorieTarget();
        if (targetKcal <= 0) throw bad("calorieTarget không hợp lệ");

        // kcal tolerance dùng để "cảnh báo" (mode 1) chứ không throw
        int tolerance = Math.max(100, (int) Math.round(targetKcal * 0.10));

        // 1) Validate candidates + build map
        Map<String, CandidateDto> candMap = new HashMap<>();
        for (CandidateDto c : candidates) {
            validateCandidateDto(c);
            candMap.putIfAbsent(c.id(), c);
        }
        if (candMap.isEmpty()) {
            throw bad("Không có candidate hợp lệ sau khi validate");
        }

        Map<Integer, DayTotals> totalsByDay = new LinkedHashMap<>();
        Set<Integer> seenDayIndex = new HashSet<>();

        for (MealPlanAiOutput.DayOutput day : aiOut.days()) {
            if (day == null) throw bad("day null");
            int dayIndex = day.dayIndex();

            if (dayIndex <= 0) throw bad("dayIndex không hợp lệ");
            if (dayIndex > expectedDays) throw bad("dayIndex vượt quá số ngày của period: " + dayIndex + " > " + expectedDays);
            if (!seenDayIndex.add(dayIndex)) throw bad("Trùng dayIndex: " + dayIndex);

            if (day.meals() == null) throw bad("meals trống");
            if (day.meals().size() != mealsPerDay) {
                throw bad("Số bữa/ngày không đúng mealsPerDay");
            }

            int kcal = 0;
            int cost = 0;

            // ===== pass 1: tính theo servings AI trả về =====
            for (MealPlanAiOutput.MealOutput meal : day.meals()) {
                if (meal == null) throw bad("meal null");
                if (meal.mealOrder() <= 0) throw bad("mealOrder không hợp lệ");

                if (meal.items() == null || meal.items().isEmpty()) {
                    throw bad("Meal items trống");
                }

                for (MealPlanAiOutput.ItemOutput item : meal.items()) {
                    if (item == null) throw bad("item null");

                    String candidateId = item.candidateId();
                    if (candidateId == null || candidateId.isBlank()) throw bad("candidateId trống");
                    validateCandidateIdFormat(candidateId);

                    if (item.servings() <= 0) throw bad("servings phải > 0");

                    CandidateDto cand = candMap.get(candidateId);
                    if (cand == null) throw bad("candidateId không tồn tại trong candidates: " + candidateId);

                    double s = item.servings();
                    kcal += (int) Math.round(cand.kcal() * s);
                    cost += (int) Math.round(cand.costVnd() * s);
                }
            }

            // ===== MODE 1: budget là hard constraint =====
            // Nếu vượt budget, scale DOWN để vừa budget (không quan tâm kcal đạt hay không)
            if (budget > 0 && cost > budget) {
                double ratioByBudget = (double) budget / Math.max(1, cost);
                double ratio = clamp(ratioByBudget, MIN_DAY_SCALE_RATIO, 1.0); // chỉ scale xuống

                int kcal2 = 0;
                int cost2 = 0;

                for (MealPlanAiOutput.MealOutput meal : day.meals()) {
                    for (MealPlanAiOutput.ItemOutput item : meal.items()) {
                        CandidateDto cand = candMap.get(item.candidateId());

                        double scaledServings = item.servings() * ratio;
                        scaledServings = clamp(scaledServings, MIN_SERVINGS_PER_ITEM, MAX_SERVINGS_PER_ITEM);

                        kcal2 += (int) Math.round(cand.kcal() * scaledServings);
                        cost2 += (int) Math.round(cand.costVnd() * scaledServings);
                    }
                }

                // Nếu vẫn vượt budget dù đã scale (do clamp MIN_SERVINGS) => fail thật
                if (cost2 > budget) {
                    throw bad("Không thể đưa plan về trong budget do MIN_SERVINGS clamp: "
                            + cost2 + " > " + budget + " (ratio=" + String.format(Locale.US, "%.2f", ratio) + ")");
                }

                kcal = kcal2;
                cost = cost2;
            }

            // ===== MODE 1: kcal là soft constraint =====
            // Nếu thiếu kcal, thử scale UP nhưng KHÔNG vượt budget
            if (Math.abs(kcal - targetKcal) > tolerance) {
                double ratioByKcal = (double) targetKcal / Math.max(1, kcal);
                double ratio = ratioByKcal;

                if (budget > 0) {
                    // scale lên nhưng không được vượt budget
                    double ratioByBudget = (double) budget / Math.max(1, cost);
                    ratio = Math.min(ratioByKcal, ratioByBudget);
                }

                ratio = clamp(ratio, 1.0, MAX_DAY_SCALE_RATIO); // chỉ scale lên (>=1)

                if (ratio > 1.000001) {
                    int kcal2 = 0;
                    int cost2 = 0;

                    for (MealPlanAiOutput.MealOutput meal : day.meals()) {
                        for (MealPlanAiOutput.ItemOutput item : meal.items()) {
                            CandidateDto cand = candMap.get(item.candidateId());

                            double scaledServings = item.servings() * ratio;
                            scaledServings = clamp(scaledServings, MIN_SERVINGS_PER_ITEM, MAX_SERVINGS_PER_ITEM);

                            kcal2 += (int) Math.round(cand.kcal() * scaledServings);
                            cost2 += (int) Math.round(cand.costVnd() * scaledServings);
                        }
                    }

                    // Chỉ accept nếu vẫn trong budget
                    if (budget <= 0 || cost2 <= budget) {
                        kcal = kcal2;
                        cost = cost2;
                    }
                }

                // Sau mọi cố gắng vẫn lệch -> KHÔNG throw, chỉ warn
                if (Math.abs(kcal - targetKcal) > tolerance) {
                    log.warn("MealPlan kcal lệch target (mode=budget-first): dayIndex={}, kcal={}, target={}, tolerance={}, cost={}, budget={}",
                            dayIndex, kcal, targetKcal, tolerance, cost, budget);
                }
            }

            totalsByDay.put(dayIndex, new DayTotals(kcal, cost));
        }

        if (requestedPeriod != PlanPeriod.DAY && totalsByDay.size() < expectedDays) {
            throw bad("AI trả thiếu số ngày: " + totalsByDay.size() + "/" + expectedDays);
        }

        return totalsByDay;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private void validateCandidateDto(CandidateDto c) {
        if (c == null) throw bad("Candidate null");
        if (c.id() == null || c.id().isBlank()) throw bad("Candidate id trống");

        String prefix = validateCandidateIdFormat(c.id());

        if (c.type() == null || c.type().isBlank()) throw bad("Candidate type trống");
        String type = c.type().trim().toUpperCase(Locale.ROOT);
        if (!type.equals("RECIPE") && !type.equals("FOOD")) {
            throw bad("Candidate type không hợp lệ: " + c.type());
        }

        if (prefix.equals("R_") && !type.equals("RECIPE")) {
            throw bad("Candidate type không khớp prefix R_: " + c.type());
        }
        if (prefix.equals("F_") && !type.equals("FOOD")) {
            throw bad("Candidate type không khớp prefix F_: " + c.type());
        }

        if (c.name() == null || c.name().isBlank()) throw bad("Candidate name trống");

        if (c.kcal() <= 0) throw bad("Candidate kcal phải > 0: " + c.id());
        if (c.proteinG() < 0 || c.fatG() < 0 || c.carbG() < 0) {
            throw bad("Candidate macro không hợp lệ: " + c.id());
        }
        if (c.costVnd() < 0) throw bad("Candidate costVnd không hợp lệ: " + c.id());
    }

    private String validateCandidateIdFormat(String candidateId) {
        String id = candidateId.trim();
        if (!(id.startsWith("R_") || id.startsWith("F_"))) {
            throw bad("candidateId sai prefix (phải R_ hoặc F_): " + candidateId);
        }
        if (id.length() < 3) throw bad("candidateId không hợp lệ: " + candidateId);

        String uuidPart = id.substring(2);
        try {
            UUID.fromString(uuidPart);
        } catch (Exception e) {
            throw bad("candidateId UUID không hợp lệ: " + candidateId);
        }
        return id.substring(0, 2);
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
