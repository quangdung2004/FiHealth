package com.backend.nutri_ai.assessment.controller;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.service.NutritionAssessmentService;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repo.AppUserRepo;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class NutritionAssessmentController {

    private final NutritionAssessmentService assessmentService;
    private final AppUserRepo appUserRepo;

    @PostMapping("/full")
    public NutritionAssessment createFull(
            Principal principal,
            @RequestBody CreateAssessmentRequest req
    ) {
        String username = principal.getName(); // user đang login

        AppUser user = appUserRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return assessmentService.createFullAssessment(user, req);
    }



    @GetMapping
    public List<NutritionAssessment> list(
            @RequestParam(required = false) Boolean me,
            Principal principal
    ) {
        if (Boolean.TRUE.equals(me)) {
            String email = principal.getName();

            AppUser user = appUserRepo.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            return assessmentService.getMyAssessments(user);
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "me=true is required"
        );
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public NutritionAssessment getById(
            @PathVariable UUID id,
            Principal principal
    ) {
        String email = principal.getName();

        AppUser user = appUserRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return assessmentService.getById(id, user);
    }

}


