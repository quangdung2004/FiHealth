package com.backend.nutri_ai.plan.controller;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.plan.dto.CreateMealPlanFromTemplateRequest;
import com.backend.nutri_ai.plan.dto.MealPlanDto;
import com.backend.nutri_ai.plan.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final AppUserRepository userRepo;

    private AppUser currentUser(Principal principal) {
        // JWT subject đang là UUID -> principal.getName() = userId
        UUID userId = UUID.fromString(principal.getName());
        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping("/from-template")
    public MealPlanDto createFromTemplate(
            Principal principal,
            @RequestParam UUID assessmentId,
            @RequestParam PlanPeriod period,
            @RequestBody(required = false) CreateMealPlanFromTemplateRequest req
    ) {
        AppUser user = currentUser(principal);
        return mealPlanService.createFromTemplate(user, assessmentId, period, req);
    }

    @GetMapping("/{id}")
    public MealPlanDto get(@PathVariable UUID id, Principal principal) {
        AppUser user = currentUser(principal);
        return mealPlanService.getPlan(user, id);
    }

    @PostMapping("/{id}/favorite")
    public Map<String, Object> toggleFavorite(@PathVariable UUID id, Principal principal) {
        AppUser user = currentUser(principal);
        return mealPlanService.toggleFavorite(user, id);
    }

    @GetMapping("/hot")
    public List<MealPlanDto> hot(
            Principal principal,
            @RequestParam PlanPeriod period,
            @RequestParam(defaultValue = "10") int limit
    ) {
        AppUser user = currentUser(principal);
        return mealPlanService.getHot(period, limit, user);
    }
}
