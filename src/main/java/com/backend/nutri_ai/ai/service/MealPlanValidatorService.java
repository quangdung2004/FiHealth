package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanValidatorService {

    public record DayTotals(int kcal, int costVnd) {}

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

        int tolerance = Math.max(100, (int) Math.round(targetKcal * 0.10)); // ±10% hoặc tối thiểu 100

        // 1) Validate candidates + build map
        Map<String, CandidateDto> candMap = new HashMap<>();
        for (CandidateDto c : candidates) {
            validateCandidateDto(c);
            // nếu trùng id thì giữ cái đầu tiên (hoặc bạn có thể throw)
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

            for (MealPlanAiOutput.MealOutput meal : day.meals()) {
                if (meal == null) throw bad("meal null");
                if (meal.mealOrder() <= 0) throw bad("mealOrder không hợp lệ");

                if (meal.items() == null || meal.items().isEmpty()) {
                    throw bad("Meal items trống");
                }

                for (MealPlanAiOutput.ItemOutput item : meal.items()) {
                    if (item == null) throw bad("item null");

                    String candidateId = item.candidateId();
                    if (candidateId == null || candidateId.isBlank()) {
                        throw bad("candidateId trống");
                    }
                    validateCandidateIdFormat(candidateId); // <-- thêm validate prefix + UUID

                    if (item.servings() <= 0) {
                        throw bad("servings phải > 0");
                    }

                    CandidateDto cand = candMap.get(candidateId);
                    if (cand == null) {
                        throw bad("candidateId không tồn tại trong candidates: " + candidateId);
                    }

                    double s = item.servings();
                    kcal += (int) Math.round(cand.kcal() * s);
                    cost += (int) Math.round(cand.costVnd() * s);
                }
            }

            if (budget > 0 && cost > budget) {
                throw bad("Vượt ngân sách/ngày: " + cost + " > " + budget);
            }

            if (Math.abs(kcal - targetKcal) > tolerance) {
                throw bad("Kcal/ngày lệch target quá nhiều: " + kcal + " vs " + targetKcal);
            }

            totalsByDay.put(dayIndex, new DayTotals(kcal, cost));
        }

        // DAY: ok nếu AI trả đúng 1 ngày
        // WEEK/MONTH: bắt buộc đủ ngày (MVP)
        if (requestedPeriod != PlanPeriod.DAY && totalsByDay.size() < expectedDays) {
            throw bad("AI trả thiếu số ngày: " + totalsByDay.size() + "/" + expectedDays);
        }

        return totalsByDay;
    }

    private void validateCandidateDto(CandidateDto c) {
        if (c == null) throw bad("Candidate null");
        if (c.id() == null || c.id().isBlank()) throw bad("Candidate id trống");

        // id format + UUID
        String prefix = validateCandidateIdFormat(c.id());

        // type
        if (c.type() == null || c.type().isBlank()) throw bad("Candidate type trống");
        String type = c.type().trim().toUpperCase(Locale.ROOT);
        if (!type.equals("RECIPE") && !type.equals("FOOD")) {
            throw bad("Candidate type không hợp lệ: " + c.type());
        }

        // prefix must match type
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
        // servingUnit có thể blank, nhưng nên normalize trước khi gửi AI (ở CandidateService)
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
            case MONTH -> 30; // MVP fixed 30, v2 làm theo tháng thật
        };
    }

    private BadRequestException bad(String msg) {
        return new BadRequestException(String.valueOf(HttpStatus.BAD_REQUEST), msg);
    }
}
