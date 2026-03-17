package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.repository.NutritionAssessmentRepository;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.catalog.entity.*;
import com.backend.nutri_ai.catalog.repository.*;
import com.backend.nutri_ai.plan.dto.CreateMealPlanFromTemplateRequest;
import com.backend.nutri_ai.plan.dto.MealPlanDetailDto;
import com.backend.nutri_ai.plan.dto.MealPlanDto;
import com.backend.nutri_ai.plan.entity.*;
import com.backend.nutri_ai.common.enums.MealType;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.plan.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final NutritionAssessmentRepository assessmentRepo;

    // template repos (catalog)
    private final MenuTemplateRepository menuTemplateRepo;
    private final TemplateDayRepository templateDayRepo;
    private final TemplateMealRepository templateMealRepo;
    private final TemplateItemRepository templateItemRepo;

    // plan repos
    private final MealPlanRepository mealPlanRepo;
    private final FavoriteMealPlanRepository favoriteRepo;
    private final HotMealPlanRepository hotRepo;

    @Transactional
    public MealPlanDto createFromTemplate(AppUser user, UUID assessmentId, PlanPeriod period, CreateMealPlanFromTemplateRequest req) {
        NutritionAssessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));

        if (!assessment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Forbidden");
        }

        MenuTemplate template = pickTemplate(period);

        LocalDate startDate = (req != null && req.getStartDate() != null) ? req.getStartDate() : LocalDate.now();
        LocalDate endDate = (period == PlanPeriod.DAY) ? startDate : startDate.plusDays(6);

        MealPlan plan = new MealPlan();
        plan.setUser(user);
        plan.setAssessment(assessment);
        plan.setPeriod(period);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);

        // ✅ FIX: set budget_per_day_vnd để khỏi lỗi DB NOT NULL
        Integer budget = null;

// 1) lấy từ assessment
        if (assessment.getBudgetPerDayVnd() != null) {
            budget = assessment.getBudgetPerDayVnd();
        }

// 2) fallback từ template
        if (budget == null && template.getBudgetPerDayVnd() != null) {
            budget = template.getBudgetPerDayVnd();
        }

// 3) vẫn null thì báo lỗi rõ ràng
        if (budget == null) {
            throw new IllegalStateException("Missing budgetPerDayVnd: assessment/template has no budget");
        }

        plan.setBudgetPerDayVnd(budget);

        // ✅ FIX: set estimated_total_cost_vnd để khỏi lỗi DB NOT NULL
        int daysCount = switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
        };

        int estimatedTotal = Math.multiplyExact(budget, daysCount);
        plan.setEstimatedTotalCostVnd(estimatedTotal);

        plan.setTotalDays(daysCount);



        List<TemplateDay> tDays = templateDayRepo.findByTemplateIdOrderByDayIndexAsc(template.getId());
        if (tDays.isEmpty()) throw new IllegalStateException("Template has no days");

        for (TemplateDay tDay : tDays) {
            PlanDay pDay = new PlanDay();
            pDay.setPlan(plan);
            pDay.setDayIndex(tDay.getDayIndex());

            LocalDate date = (period == PlanPeriod.DAY) ? startDate : startDate.plusDays(tDay.getDayIndex() - 1L);
            pDay.setDate(date);

            pDay.setCostVnd(0);
            pDay.setTotalKcal(0);

            List<TemplateMeal> tMeals = templateMealRepo.findByDayIdOrderByMealOrderAsc(tDay.getId());
            for (TemplateMeal tMeal : tMeals) {
                PlanMeal pMeal = new PlanMeal();
                pMeal.setDay(pDay);
                pMeal.setMealOrder(tMeal.getMealOrder());
                pMeal.setName(tMeal.getName());
                pMeal.setMealType(parseMealType(tMeal.getName(), tMeal.getMealOrder()));

                List<TemplateItem> tItems = templateItemRepo.findByMealId(tMeal.getId());
                for (TemplateItem tItem : tItems) {
                    boolean hasFood = tItem.getFoodItem() != null;
                    boolean hasRecipe = tItem.getRecipe() != null;

                    // XOR rule: chỉ 1 trong 2
                    if (hasFood == hasRecipe) {
                        throw new IllegalStateException("TemplateItem invalid: must have either food_item_id OR recipe_id");
                    }

                    MealItem pItem = new MealItem();
                    pItem.setMeal(pMeal);
                    pItem.setFoodItem(tItem.getFoodItem());
                    pItem.setRecipe(tItem.getRecipe());
                    pItem.setAmount(tItem.getAmountOverride());

                    pMeal.getItems().add(pItem);
                }

                pDay.getMeals().add(pMeal);
            }

            plan.getDays().add(pDay);
        }

        MealPlan saved = mealPlanRepo.save(plan);
        boolean favorite = favoriteRepo.findByUserIdAndMealPlanId(user.getId(), saved.getId()).isPresent();
        return toDto(saved, favorite);
    }

    @Transactional(readOnly = true)
    public MealPlanDto getPlan(AppUser user, UUID planId) {
        MealPlan plan = mealPlanRepo.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        boolean favorite = favoriteRepo.findByUserIdAndMealPlanId(user.getId(), planId).isPresent();
        return toDto(plan, favorite);
    }

    @Transactional(readOnly = true)
    public MealPlanDetailDto getPlanDetail(AppUser user, UUID planId) {
        MealPlan plan = mealPlanRepo.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        boolean favorite = favoriteRepo.findByUserIdAndMealPlanId(user.getId(), planId).isPresent();
        return toDetailDto(plan, favorite);
    }


    @Transactional
    public Map<String, Object> toggleFavorite(AppUser user, UUID planId) {
        MealPlan plan = mealPlanRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Optional<UserFavoriteMealPlan> existing = favoriteRepo.findByUserIdAndMealPlanId(user.getId(), planId);
        boolean favorite;
        if (existing.isPresent()) {
            favoriteRepo.delete(existing.get());
            favorite = false;
        } else {
            UserFavoriteMealPlan fav = new UserFavoriteMealPlan();
            fav.setUser(user);
            fav.setMealPlan(plan);
            favoriteRepo.save(fav);
            favorite = true;
        }
        return Map.of("mealPlanId", planId, "favorite", favorite);
    }

    @Transactional(readOnly = true)
    public List<MealPlanDto> getHot(PlanPeriod period, int limit, AppUser viewer) {
        List<MealPlan> plans = hotRepo.findHot(period, PageRequest.of(0, Math.max(1, Math.min(limit, 50))));

        Set<UUID> favIds = new HashSet<>();
        for (MealPlan p : plans) {
            if (favoriteRepo.findByUserIdAndMealPlanId(viewer.getId(), p.getId()).isPresent()) {
                favIds.add(p.getId());
            }
        }

        List<MealPlanDto> out = new ArrayList<>();
        for (MealPlan p : plans) out.add(toDto(p, favIds.contains(p.getId())));
        return out;
    }

    // ===== helpers =====

    private MenuTemplate pickTemplate(PlanPeriod period) {
        List<MenuTemplate> templates = menuTemplateRepo.findByPeriod(period);
        if (templates.isEmpty()) throw new IllegalStateException("No menu_template for period=" + period);
        return templates.get(new Random().nextInt(templates.size())); // MVP: random
    }

    private MealType parseMealType(String name, int mealOrder) {
        String s = (name == null) ? "" : name.trim().toLowerCase();
        if (s.contains("breakfast") || s.contains("sáng")) return MealType.BREAKFAST;
        if (s.contains("lunch") || s.contains("trưa")) return MealType.LUNCH;
        if (s.contains("dinner") || s.contains("tối")) return MealType.DINNER;
        if (s.contains("snack") || s.contains("phụ")) return MealType.SNACK;

        return switch (mealOrder) {
            case 1 -> MealType.BREAKFAST;
            case 2 -> MealType.LUNCH;
            case 3 -> MealType.DINNER;
            default -> MealType.SNACK;
        };
    }

    private MealPlanDto toDto(MealPlan plan, boolean favorite) {
        MealPlanDto dto = new MealPlanDto();
        dto.setId(plan.getId());
        dto.setPeriod(plan.getPeriod());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setFavorite(favorite);

        List<MealPlanDto.DayDto> dayDtos = new ArrayList<>();
        for (PlanDay d : plan.getDays()) {
            MealPlanDto.DayDto dDto = new MealPlanDto.DayDto();
            dDto.setDayIndex(d.getDayIndex());
            dDto.setDate(d.getDate());

            List<MealPlanDto.MealDto> mealDtos = new ArrayList<>();
            for (PlanMeal m : d.getMeals()) {
                MealPlanDto.MealDto mDto = new MealPlanDto.MealDto();
                mDto.setMealOrder(m.getMealOrder());
                mDto.setMealType(m.getMealType());
                mDto.setName(m.getName());

                List<MealPlanDto.ItemDto> itemDtos = new ArrayList<>();
                for (MealItem it : m.getItems()) {
                    MealPlanDto.ItemDto itDto = new MealPlanDto.ItemDto();
                    itDto.setAmount(it.getAmount());

                    if (it.getFoodItem() != null) {
                        itDto.setFoodItemId(it.getFoodItem().getId());
                        itDto.setFoodItemName(it.getFoodItem().getName());
                    }
                    if (it.getRecipe() != null) {
                        itDto.setRecipeId(it.getRecipe().getId());
                        itDto.setRecipeName(it.getRecipe().getName());
                    }
                    itemDtos.add(itDto);
                }
                mDto.setItems(itemDtos);
                mealDtos.add(mDto);
            }

            dDto.setMeals(mealDtos);
            dayDtos.add(dDto);
        }
        dto.setDays(dayDtos);
        return dto;
    }
    private MealPlanDetailDto toDetailDto(MealPlan plan, boolean favorite) {
        MealPlanDetailDto dto = new MealPlanDetailDto();
        dto.setId(plan.getId());
        dto.setPeriod(plan.getPeriod());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());

        dto.setTotalDays(plan.getTotalDays());
        dto.setBudgetPerDayVnd(plan.getBudgetPerDayVnd());
        dto.setEstimatedTotalCostVnd(plan.getEstimatedTotalCostVnd());

        dto.setFavorite(favorite);

        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setGoal(plan.getAssessment() != null ? plan.getAssessment().getGoal() : null);

        return dto;
    }

}
