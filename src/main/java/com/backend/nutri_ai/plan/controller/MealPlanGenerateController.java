package com.backend.nutri_ai.plan.controller;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.service.impl.auth.AuthService;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.plan.dto.MealPlanGenerateResponse;
import com.backend.nutri_ai.plan.service.MealPlanGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanGenerateController {

    private final MealPlanGenerateService generateService;
    private final AuthService authService;

    @PostMapping("/generate")
    public MealPlanGenerateResponse generate(
            @RequestParam UUID assessmentId,
            @RequestParam PlanPeriod period
    ) {
        AppUser user = authService.getAuthenticatedUser();
        return generateService.generate(user, assessmentId, period);
    }
}
