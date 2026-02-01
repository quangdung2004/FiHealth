package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.catalog.entity.*;
import com.backend.nutri_ai.catalog.repository.MenuTemplateRepository;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.BadRequestException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanCandidateService {

    private final MenuTemplateRepository templateRepo;

    public record CandidateBundle(
            MenuTemplate template,
            List<CandidateDto> candidates,
            Map<String, CandidateRef> refMap
    ) {}

    public record CandidateRef(
            String type, // "RECIPE" | "FOOD"
            UUID id
    ) {}

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

        for (TemplateItem item : templateItems) {
            if (item.getRecipe() != null) {
                Recipe r = item.getRecipe();
                if (!Boolean.TRUE.equals(r.getActive())) continue;
                if (containsAllergen(r, allergyCodes)) continue;

                String cid = "R_" + r.getId();
                if (!refMap.containsKey(cid)) {
                    candidates.add(new CandidateDto(
                            cid, "RECIPE", r.getName(),
                            r.getKcal(), r.getProteinG(), r.getFatG(), r.getCarbG(),
                            r.getEstimatedCostVnd(),
                            "1 serving"
                    ));
                    refMap.put(cid, new CandidateRef("RECIPE", r.getId()));
                }
            } else if (item.getFoodItem() != null) {
                FoodItem f = item.getFoodItem();
                if (!Boolean.TRUE.equals(f.getActive())) continue;
                if (containsAllergen(f, allergyCodes)) continue;

                String cid = "F_" + f.getId();
                if (!refMap.containsKey(cid)) {
                    candidates.add(new CandidateDto(
                            cid, "FOOD", f.getName(),
                            f.getKcalPerServing(), f.getProteinG(), f.getFatG(), f.getCarbG(),
                            f.getEstimatedPriceVndPerServing(),
                            (f.getServingSize() != null && !f.getServingSize().isBlank()) ? f.getServingSize() : "1 serving"
                    ));
                    refMap.put(cid, new CandidateRef("FOOD", f.getId()));
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new BadRequestException(String.valueOf(HttpStatus.BAD_REQUEST),"Template không có món hợp lệ (có thể do filter dị ứng/bị inactive)");
        }

        // Optional: có thể bổ sung candidates dự phòng từ catalog sau (v2)
        // -> hiện MVP cứ dùng đúng template items để AI không đi chệch.

        return new CandidateBundle(template, candidates, refMap);
    }

    private PlanPeriod chooseTemplatePeriod(PlanPeriod requested) {
        // Template của bạn ghi DAY/WEEK, MONTH thì sẽ dùng WEEK làm base (repeat)
        if (requested == PlanPeriod.MONTH) return PlanPeriod.WEEK;
        return requested;
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
        if (foodItem.getAllergens() == null) return false;
        return foodItem.getAllergens().stream()
                .map(FoodAllergen::getCode)
                .filter(Objects::nonNull)
                .map(c -> c.toUpperCase(Locale.ROOT))
                .anyMatch(allergyCodes::contains);
    }

    private boolean containsAllergen(Recipe recipe, Set<String> allergyCodes) {
        if (allergyCodes.isEmpty()) return false;
        if (recipe.getIngredients() == null) return false;

        for (RecipeIngredient ing : recipe.getIngredients()) {
            FoodItem fi = ing.getFoodItem();
            if (fi != null && containsAllergen(fi, allergyCodes)) return true;
        }
        return false;
    }
}
