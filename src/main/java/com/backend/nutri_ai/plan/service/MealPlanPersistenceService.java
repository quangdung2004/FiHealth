package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.ai.service.MealPlanValidatorService;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
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

    // 1 bữa = đúng 3 món
    private static final int ITEMS_PER_MEAL = 3;

    // clamp servings
    private static final double MIN_SERVINGS = 0.5;
    private static final double MAX_SERVINGS = 2.0;

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
        if (aiOut == null || aiOut.days() == null || aiOut.days().isEmpty())
            throw new BadRequestException("AI output trống");

        // ===== candidate map + recipe pool =====
        Map<String, CandidateDto> candMap = new HashMap<>();
        List<CandidateDto> recipePool = new ArrayList<>();

        for (CandidateDto c : candidates) {
            if (c == null || c.id() == null) continue;
            candMap.putIfAbsent(c.id(), c);
            if ("RECIPE".equalsIgnoreCase(c.type())) {
                recipePool.add(c);
            }
        }

        if (recipePool.isEmpty())
            throw new BadRequestException("Không có RECIPE candidate");

        // ===== plan meta =====
        LocalDate start = LocalDate.now();
        int totalDays = switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
        };

        MealPlan plan = new MealPlan();
        plan.setUser(user);
        plan.setAssessment(assessment);
        plan.setPeriod(period);
        plan.setStartDate(start);
        plan.setEndDate(start.plusDays(totalDays - 1));
        plan.setTotalDays(totalDays);
        plan.setBudgetPerDayVnd(
                assessment.getBudgetPerDayVnd() != null ? assessment.getBudgetPerDayVnd() : 0
        );
        plan.setDays(new HashSet<>());

        int totalCost = 0;

        // ===== dedupe dayIndex =====
        Map<Integer, MealPlanAiOutput.DayOutput> dayMap = new LinkedHashMap<>();
        for (MealPlanAiOutput.DayOutput d : aiOut.days()) {
            if (d == null) continue;
            if (d.dayIndex() <= 0 || d.dayIndex() > totalDays) continue;
            dayMap.putIfAbsent(d.dayIndex(), d);
        }

        // ===== build days =====
        for (var entry : dayMap.entrySet()) {
            int dayIndex = entry.getKey();
            MealPlanAiOutput.DayOutput d = entry.getValue();

            PlanDay planDay = new PlanDay();
            planDay.setPlan(plan);
            planDay.setDayIndex(dayIndex);
            planDay.setDate(start.plusDays(dayIndex - 1));
            planDay.setMeals(new HashSet<>());

            // chống trùng recipe trong cả ngày
            Set<UUID> usedRecipeIdsInDay = new HashSet<>();

            MealPlanValidatorService.DayTotals totals =
                    totalsByDay != null ? totalsByDay.get(dayIndex) : null;

            planDay.setTotalKcal(totals != null ? totals.kcal() : 0);
            planDay.setCostVnd(totals != null ? totals.costVnd() : 0);
            totalCost += planDay.getCostVnd();

            // ===== dedupe mealOrder =====
            Map<Integer, MealPlanAiOutput.MealOutput> mealMap = new LinkedHashMap<>();
            for (MealPlanAiOutput.MealOutput m : d.meals()) {
                if (m == null || m.mealOrder() <= 0) continue;
                mealMap.putIfAbsent(m.mealOrder(), m);
            }

            for (var mEntry : mealMap.entrySet()) {
                MealPlanAiOutput.MealOutput m = mEntry.getValue();

                PlanMeal planMeal = new PlanMeal();
                planMeal.setDay(planDay);
                planMeal.setMealOrder(m.mealOrder());
                planMeal.setName(firstNonBlank(m.name(), defaultMealName(m.mealOrder())));
                planMeal.setMealType(inferMealType(m.mealOrder(), planMeal.getName()));
                planMeal.setItems(new HashSet<>());

                int mealKcal = 0;
                int mealCost = 0;

                // chống trùng trong 1 meal
                Set<UUID> usedRecipeIdsInMeal = new HashSet<>();

                List<MealPlanAiOutput.ItemOutput> items =
                        new ArrayList<>(m.items() != null ? m.items() : List.of());
                Collections.shuffle(items);

                int added = 0;

                // ===== PASS 1: theo AI =====
                for (MealPlanAiOutput.ItemOutput it : items) {
                    if (added >= ITEMS_PER_MEAL) break;
                    if (it == null || it.recipeCandidateId() == null) continue;

                    CandidateDto cand = candMap.get(it.recipeCandidateId());
                    if (cand == null || !"RECIPE".equalsIgnoreCase(cand.type())) continue;

                    UUID rid = extractUuid(cand.id());
                    if (!usedRecipeIdsInMeal.add(rid)) continue;
                    if (!usedRecipeIdsInDay.add(rid)) continue;

                    double servings = clamp(
                            it.servings() != null ? it.servings() : 1.0,
                            MIN_SERVINGS, MAX_SERVINGS
                    );

                    int itemKcal = (int) Math.round(cand.kcal() * servings);
                    int itemCost = (int) Math.round(cand.costVnd() * servings);

                    mealKcal += itemKcal;
                    mealCost += itemCost;

                    planMeal.getItems().add(buildItem(planMeal, cand, rid, servings, itemKcal, itemCost));
                    added++;
                }

                // ===== PASS 2: auto-fill nếu thiếu =====

// 2.1) ưu tiên món chưa dùng trong ngày
                Collections.shuffle(recipePool);
                for (CandidateDto c : recipePool) {
                    if (added >= ITEMS_PER_MEAL) break;

                    UUID rid = extractUuid(c.id());

                    // không trùng trong meal
                    if (!usedRecipeIdsInMeal.add(rid)) continue;

                    // ưu tiên chưa dùng trong ngày
                    if (!usedRecipeIdsInDay.add(rid)) continue;

                    double servings = 1.0;
                    servings = clamp(servings, MIN_SERVINGS, MAX_SERVINGS);

                    int itemKcal = (int) Math.round(c.kcal() * servings);
                    int itemCost = (int) Math.round(c.costVnd() * servings);

                    mealKcal += itemKcal;
                    mealCost += itemCost;

                    planMeal.getItems().add(buildItem(planMeal, c, rid, servings, itemKcal, itemCost));
                    added++;
                }

// 2.2) fallback: nếu vẫn thiếu thì cho phép reuse trong ngày (nhưng vẫn không trùng trong meal)
                if (added < ITEMS_PER_MEAL) {
                    Collections.shuffle(recipePool);
                    for (CandidateDto c : recipePool) {
                        if (added >= ITEMS_PER_MEAL) break;

                        UUID rid = extractUuid(c.id());

                        // không trùng trong meal
                        if (!usedRecipeIdsInMeal.add(rid)) continue;

                        double servings = 1.0;
                        servings = clamp(servings, MIN_SERVINGS, MAX_SERVINGS);

                        int itemKcal = (int) Math.round(c.kcal() * servings);
                        int itemCost = (int) Math.round(c.costVnd() * servings);

                        mealKcal += itemKcal;
                        mealCost += itemCost;

                        planMeal.getItems().add(buildItem(planMeal, c, rid, servings, itemKcal, itemCost));
                        added++;
                    }
                }

                if (added != ITEMS_PER_MEAL) {
                    throw new BadRequestException("Không đủ 3 món cho bữa " + m.mealOrder()
                            + " (recipePool=" + recipePool.size() + ")");
                }


                planMeal.setKcal(mealKcal);
                planMeal.setCostVnd(mealCost);
                planDay.getMeals().add(planMeal);
            }

            plan.getDays().add(planDay);
        }

        plan.setEstimatedTotalCostVnd(totalCost);
        return mealPlanRepository.save(plan);
    }

    // ===== helpers =====

    private MealItem buildItem(
            PlanMeal meal, CandidateDto cand, UUID rid,
            double servings, int kcal, int cost
    ) {
        MealItem item = new MealItem();
        item.setMeal(meal);
        item.setRecipe(em.getReference(Recipe.class, rid));
        item.setFoodItem(null);
        item.setFoodName(cand.name());
        item.setKcal(kcal);
        item.setCostVnd(cost);
        item.setProteinG((int) Math.round(cand.proteinG() * servings));
        item.setFatG((int) Math.round(cand.fatG() * servings));
        item.setCarbG((int) Math.round(cand.carbG() * servings));
        item.setAmount(formatAmount(servings));
        return item;
    }

    private UUID extractUuid(String cid) {
        return UUID.fromString(cid.substring(2));
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private String formatAmount(double s) {
        return (s == (long) s ? (long) s : s) + " x serving";
    }

    private static String firstNonBlank(String... v) {
        for (String s : v) if (s != null && !s.isBlank()) return s;
        return "";
    }

    private String defaultMealName(int order) {
        return switch (order) {
            case 1 -> "Bữa sáng";
            case 2 -> "Bữa trưa";
            case 3 -> "Bữa tối";
            default -> "Bữa phụ";
        };
    }

    private MealType inferMealType(int order, String name) {
        String n = name.toLowerCase();
        if (n.contains("sáng")) return MealType.BREAKFAST;
        if (n.contains("trưa")) return MealType.LUNCH;
        if (n.contains("tối")) return MealType.DINNER;
        return MealType.SNACK;
    }
}
