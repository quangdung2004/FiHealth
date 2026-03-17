package com.backend.nutri_ai.plan.controller;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.service.impl.auth.AuthService;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.plan.dto.MealPlanDetailResponse;
import com.backend.nutri_ai.plan.dto.MealPlanGenerateResponse;
import com.backend.nutri_ai.plan.service.MealPlanGenerateService;
import com.backend.nutri_ai.plan.service.MealPlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanGenerateController {

    private final MealPlanGenerateService generateService;
    private final AuthService authService;
    private final MealPlanQueryService queryService;


    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<MealPlanGenerateResponse>> generate(
            @RequestParam UUID assessmentId,
            @RequestParam PlanPeriod period
    ) {
        AppUser user = authService.getAuthenticatedUser();
        MealPlanGenerateResponse data = generateService.generate(user, assessmentId, period);

        return ResponseEntity.ok(ApiResponse.ok("Generated meal plan", data));
    }

    @GetMapping("/ai/{id}")
    public ResponseEntity<ApiResponse<MealPlanDetailResponse>> getDetail(
            @PathVariable UUID id
    ) {
        AppUser user = authService.getAuthenticatedUser();
        MealPlanDetailResponse data = queryService.getDetail(user, id);
        return ResponseEntity.ok(ApiResponse.ok("Meal plan detail", data));
    }

}
