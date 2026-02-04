package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.catalog.entity.*;
import com.backend.nutri_ai.catalog.repository.IRecipeRepository;
import com.backend.nutri_ai.catalog.repository.MenuTemplateRepository;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanCandidateService {

    private final MenuTemplateRepository templateRepo;
    private final IRecipeRepository recipeRepo;

    // ✅ yêu cầu: 1 bữa = 3 món
    private static final int ITEMS_PER_MEAL = 3;

    public record CandidateBundle(
            MenuTemplate template,
            List<CandidateDto> candidates,
            Map<String, CandidateRef> refMap
    ) {}

    public record CandidateRef(
            String type, // "RECIPE" | "FOOD"
            UUID id
    ) {}

    /**
     * Hướng A:
     * 1) Lấy candidates từ template graph (ưu tiên).
     * 2) Nếu thiếu -> bổ sung từ toàn bộ recipe active trong DB (lọc dị ứng) để AI có pool đủ lớn.
     */
    @Transactional(readOnly = true)
    public CandidateBundle buildCandidates(NutritionAssessment assessment, PlanPeriod requestedPeriod) {

        PlanPeriod templatePeriod = chooseTemplatePeriod(requestedPeriod);

        Integer budget = (assessment.getBudgetPerDayVnd() != null) ? assessment.getBudgetPerDayVnd() : 0;
        MenuTemplate chosen = templateRepo
                .findTopByActiveTrueAndPeriodAndGoalAndBudgetPerDayVndLessThanEqualOrderByBudgetPerDayVndDesc(
                        templatePeriod, assessment.getGoal(), budget
                )
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy menu template phù hợp"));

        MenuTemplate template = templateRepo.findByIdWithGraph(chosen.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Template không tồn tại"));

        Set<String> allergyCodes = parseAllergyCodes(assessment.getAllergies());

        List<TemplateItem> templateItems = template.getDays().stream()
                .sorted(Comparator.comparingInt(TemplateDay::getDayIndex))
                .flatMap(d -> d.getMeals().stream().sorted(Comparator.comparingInt(TemplateMeal::getMealOrder)))
                .flatMap(m -> m.getItems().stream())
                .toList();

        List<CandidateDto> candidates = new ArrayList<>();
        Map<String, CandidateRef> refMap = new LinkedHashMap<>();

        // =========================
        // 1) Seed candidates từ template
        // =========================
        for (TemplateItem item : templateItems) {
            if (item.getRecipe() == null) continue;

            Recipe r = item.getRecipe();
            if (!Boolean.TRUE.equals(r.getActive())) continue;
            if (containsAllergen(r, allergyCodes)) continue;

            String cid = "R_" + r.getId();
            if (!refMap.containsKey(cid)) {
                candidates.add(toCandidate(r));
                refMap.put(cid, new CandidateRef("RECIPE", r.getId()));
            }
        }

        // Nếu template không có gì hợp lệ -> vẫn fallback DB (đừng throw ngay)
        if (candidates.isEmpty()) {
            log.warn("Template has 0 valid recipes after allergen filter. templateId={}, allergies={}",
                    template.getId(), allergyCodes);
        }

        // =========================
        // 2) Fallback: bơm thêm từ toàn bộ recipe active trong DB nếu thiếu
        // =========================

        int mealsPerDay = (assessment.getMealsPerDay() != null ? assessment.getMealsPerDay() : 3);
        if (mealsPerDay <= 0) mealsPerDay = 3;

        // Mục tiêu tối thiểu để đỡ lặp:
        // DAY: 3 bữa * 3 món = 9 recipes
        // WEEK: 7 ngày -> 63 recipes (nếu DB không đủ thì lấy tối đa có thể)
        // MONTH: 30 ngày -> quá lớn, giới hạn 120 để nhẹ hệ thống
        int expectedDays = expectedDays(requestedPeriod);
        int varietyDays = Math.min(expectedDays, 7);                 // ưu tiên đa dạng trong 7 ngày đầu
        int minNeeded = mealsPerDay * ITEMS_PER_MEAL * varietyDays;  // ví dụ WEEK = 63
        int hardCap = Math.min(200, mealsPerDay * ITEMS_PER_MEAL * Math.min(expectedDays, 30)); // giới hạn trên

        // Nếu bạn muốn “1 ngày 3 bữa 3 món” chắc chắn đủ => min cho DAY luôn là 9
        minNeeded = Math.max(minNeeded, mealsPerDay * ITEMS_PER_MEAL);

        if (candidates.size() < minNeeded) {
            int before = candidates.size();

            // Lấy toàn bộ recipe, lọc active (đơn giản, dev-friendly).
            // Lưu ý: containsAllergen(recipe) sẽ chạm ingredients (lazy) nên cần @Transactional.
            List<Recipe> all = recipeRepo.findAll();
            List<Recipe> pool = all.stream()
                    .filter(r -> Boolean.TRUE.equals(r.getActive()))
                    // optional: đừng filter budget quá chặt, chỉ loại món quá đắt so với budget ngày
                    .filter(r -> budget <= 0 || r.getEstimatedCostVnd() == null || r.getEstimatedCostVnd() <= budget)
                    .collect(Collectors.toList());

            // random để tránh luôn “rau muống xào tỏi”
            Collections.shuffle(pool);

            for (Recipe r : pool) {
                if (candidates.size() >= hardCap) break;

                if (r == null || r.getId() == null) continue;
                if (containsAllergen(r, allergyCodes)) continue;

                String cid = "R_" + r.getId();
                if (refMap.containsKey(cid)) continue;

                candidates.add(toCandidate(r));
                refMap.put(cid, new CandidateRef("RECIPE", r.getId()));

                if (candidates.size() >= minNeeded) break;
            }

            log.warn("Candidate fallback applied. before={}, after={}, minNeeded={}, hardCap={}, templateId={}, allergies={}",
                    before, candidates.size(), minNeeded, hardCap, template.getId(), allergyCodes);
        }

        // Nếu sau fallback vẫn không có -> mới throw
        if (candidates.isEmpty()) {
            throw new BadRequestException(
                    "Không có recipe hợp lệ sau khi lọc dị ứng (allergies=" + allergyCodes + ")"
            );
        }

        return new CandidateBundle(template, candidates, refMap);
    }

    // ================= helpers =================

    private CandidateDto toCandidate(Recipe r) {
        return new CandidateDto(
                "R_" + r.getId(),
                "RECIPE",
                r.getName(),
                safeInt(r.getKcal()),
                safeInt(r.getProteinG()),
                safeInt(r.getFatG()),
                safeInt(r.getCarbG()),
                safeInt(r.getEstimatedCostVnd()),
                "1 serving"
        );
    }

    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private PlanPeriod chooseTemplatePeriod(PlanPeriod requested) {
        // Template của bạn ghi DAY/WEEK, MONTH thì dùng WEEK làm base (repeat)
        if (requested == PlanPeriod.MONTH) return PlanPeriod.WEEK;
        return requested;
    }

    private int expectedDays(PlanPeriod period) {
        return switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
        };
    }

    private Set<String> parseAllergyCodes(String allergies) {
        if (allergies == null || allergies.isBlank()) return Set.of();
        return Arrays.stream(allergies.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    private boolean containsAllergen(FoodItem foodItem, Set<String> allergyCodes) {
        if (allergyCodes.isEmpty()) return false;
        if (foodItem == null) return false;
        if (foodItem.getAllergens() == null) return false;
        return foodItem.getAllergens().stream()
                .map(FoodAllergen::getCode)
                .filter(Objects::nonNull)
                .map(c -> c.toUpperCase(Locale.ROOT))
                .anyMatch(allergyCodes::contains);
    }

    private boolean containsAllergen(Recipe recipe, Set<String> allergyCodes) {
        if (allergyCodes.isEmpty()) return false;
        if (recipe == null) return false;
        if (recipe.getIngredients() == null) return false;

        for (RecipeIngredient ing : recipe.getIngredients()) {
            FoodItem fi = (ing != null ? ing.getFoodItem() : null);
            if (fi != null && containsAllergen(fi, allergyCodes)) return true;
        }
        return false;
    }
}
