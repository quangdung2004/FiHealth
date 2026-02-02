package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.ai.service.MealPlanValidatorService;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.common.enums.MealType;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import com.backend.nutri_ai.plan.entity.MealItem;
import com.backend.nutri_ai.plan.entity.MealPlan;
import com.backend.nutri_ai.plan.entity.PlanDay;
import com.backend.nutri_ai.plan.entity.PlanMeal;
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
            Map<Integer, MealPlanValidatorService.DayTotals> totalsByDay
    ) {
        if (user == null) throw new BadRequestException("Thiếu user");
        if (assessment == null) throw new BadRequestException("Thiếu assessment");
        if (period == null) throw new BadRequestException("Thiếu period");
        if (aiOut == null || aiOut.days() == null || aiOut.days().isEmpty())
            throw new BadRequestException("AI output trống");
        if (candidates == null || candidates.isEmpty())
            throw new BadRequestException("Danh sách candidates trống");

        Map<String, CandidateDto> candMap = new HashMap<>();
        for (CandidateDto c : candidates) {
            if (c != null && c.id() != null) candMap.put(c.id(), c);
        }

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
            if (d == null) continue;

            int dayIndex = d.dayIndex();
            if (dayIndex <= 0 || dayIndex > totalDays) continue;

            LocalDate date = start.plusDays(dayIndex - 1);

            PlanDay planDay = new PlanDay();
            planDay.setPlan(plan);
            planDay.setDayIndex(dayIndex);
            planDay.setDate(date);

            MealPlanValidatorService.DayTotals totals = (totalsByDay != null ? totalsByDay.get(dayIndex) : null);
            int dayKcal = (totals != null ? totals.kcal() : 0);
            int dayCost = (totals != null ? totals.costVnd() : 0);

            planDay.setTotalKcal(dayKcal);
            planDay.setCostVnd(dayCost);
            totalCost += dayCost;

            List<MealPlanAiOutput.MealOutput> meals =
                    new ArrayList<>(d.meals() != null ? d.meals() : List.of());
            meals.sort(Comparator.comparingInt(MealPlanAiOutput.MealOutput::mealOrder));

            for (MealPlanAiOutput.MealOutput m : meals) {
                if (m == null) continue;

                PlanMeal planMeal = new PlanMeal();
                planMeal.setDay(planDay);
                planMeal.setMealOrder(m.mealOrder());
                planMeal.setName(m.name() != null ? m.name() : defaultMealName(m.mealOrder()));
                planMeal.setMealType(inferMealType(m.mealOrder(), planMeal.getName()));

                int mealKcal = 0;
                int mealCost = 0;

                List<MealPlanAiOutput.ItemOutput> items =
                        (m.items() != null ? m.items() : List.of());

                for (MealPlanAiOutput.ItemOutput it : items) {
                    if (it == null) continue;

                    CandidateDto cand = candMap.get(it.candidateId());
                    if (cand == null) continue;

                    double servings = it.servings();
                    if (servings <= 0) continue;

                    // ✅ FIX: cand.kcal()/cand.costVnd() là int => cast sang double khi nhân
                    int itemKcal = (int) Math.round(((double) cand.kcal()) * servings);
                    int itemCost = (int) Math.round(((double) cand.costVnd()) * servings);

                    mealKcal += itemKcal;
                    mealCost += itemCost;

                    MealItem item = new MealItem();
                    item.setMeal(planMeal);

                    UUID refId = extractUuidFromCandidateId(cand.id());
                    if ("RECIPE".equalsIgnoreCase(nullSafeString(cand.type()))) {
                        item.setRecipe(em.getReference(Recipe.class, refId));
                        item.setFoodItem(null);
                    } else {
                        item.setFoodItem(em.getReference(FoodItem.class, refId));
                        item.setRecipe(null);
                    }

                    // ✅ Snapshot để tránh lỗi DB NOT NULL (food_name/cost_vnd/kcal...)
                    String snapshotName = firstNonBlank(
                            safeCandidateName(cand),
                            planMeal.getName(),
                            "Unknown"
                    );
                    item.setFoodName(snapshotName);

                    item.setKcal(itemKcal);
                    item.setCostVnd(itemCost);

                    // macro: nếu CandidateDto có thì dùng, không có => 0
                    item.setProteinG((int) Math.round(safeGetProteinG(cand) * servings));
                    item.setFatG((int) Math.round(safeGetFatG(cand) * servings));
                    item.setCarbG((int) Math.round(safeGetCarbG(cand) * servings));

                    item.setAmount(formatAmount(servings, safeServingUnit(cand)));
                    planMeal.getItems().add(item);
                }

                // nếu PlanMeal có các field NOT NULL trong DB thì set luôn
                planMeal.setCostVnd(mealCost);
                planMeal.setKcal(mealKcal);

                planDay.getMeals().add(planMeal);
            }

            plan.getDays().add(planDay);
        }

        plan.setEstimatedTotalCostVnd(totalCost);
        return mealPlanRepository.save(plan);
    }

    private UUID extractUuidFromCandidateId(String candidateId) {
        if (candidateId == null || candidateId.length() < 3) {
            throw new BadRequestException("candidateId không hợp lệ: " + candidateId);
        }
        try {
            return UUID.fromString(candidateId.substring(2));
        } catch (Exception e) {
            throw new BadRequestException("candidateId UUID không hợp lệ: " + candidateId);
        }
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

    // ---------------- helpers ----------------

    private static String nullSafeString(String v) {
        return v == null ? "" : v;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return "";
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return "";
    }

    private static String safeCandidateName(CandidateDto cand) {
        if (cand == null) return null;
        try {
            var m = cand.getClass().getMethod("name");
            Object val = m.invoke(cand);
            return val != null ? val.toString() : null;
        } catch (Exception ignore) {}
        return cand.id();
    }

    private static String safeServingUnit(CandidateDto cand) {
        if (cand == null) return null;
        try {
            var m = cand.getClass().getMethod("servingUnit");
            Object val = m.invoke(cand);
            return val != null ? val.toString() : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private static double safeGetProteinG(CandidateDto cand) {
        if (cand == null) return 0.0;
        try {
            var m = cand.getClass().getMethod("proteinG");
            Object val = m.invoke(cand);
            if (val instanceof Number n) return n.doubleValue();
        } catch (Exception ignore) {}
        return 0.0;
    }

    private static double safeGetFatG(CandidateDto cand) {
        if (cand == null) return 0.0;
        try {
            var m = cand.getClass().getMethod("fatG");
            Object val = m.invoke(cand);
            if (val instanceof Number n) return n.doubleValue();
        } catch (Exception ignore) {}
        return 0.0;
    }

    private static double safeGetCarbG(CandidateDto cand) {
        if (cand == null) return 0.0;
        try {
            var m = cand.getClass().getMethod("carbG");
            Object val = m.invoke(cand);
            if (val instanceof Number n) return n.doubleValue();
        } catch (Exception ignore) {}
        return 0.0;
    }
}
