package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.catalog.repository.IRecipeRepository;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.plan.dto.MealPlanDetailResponse;
import com.backend.nutri_ai.plan.entity.MealPlan;
import com.backend.nutri_ai.plan.mapper.MealPlanDetailMapper;
import com.backend.nutri_ai.plan.repo.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanQueryService {

    private final MealPlanRepository mealPlanRepo;
    private final IRecipeRepository recipeRepo;

    @Transactional(readOnly = true)
    public MealPlanDetailResponse getDetail(AppUser user, UUID id) {

        MealPlan plan = mealPlanRepo
                .findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy meal plan"));

        Set<UUID> recipeIds = plan.getDays().stream()
                .flatMap(d -> d.getMeals().stream())
                .flatMap(m -> m.getItems().stream())
                .map(it -> it.getRecipe() != null ? it.getRecipe().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Recipe> recipeMap = recipeIds.isEmpty()
                ? Map.of()
                : recipeRepo.findAllWithIngredients(new ArrayList<>(recipeIds))
                .stream()
                .collect(Collectors.toMap(r -> r.getId(), r -> r));

        return MealPlanDetailMapper.toDto(plan, recipeMap);
    }
}
