package com.backend.nutri_ai.assessment.controller;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.dto.NutritionAssessmentResponse;
import com.backend.nutri_ai.assessment.service.NutritionAssessmentService;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class NutritionAssessmentController {

    private final NutritionAssessmentService assessmentService;
    private final AppUserRepository appUserRepo;

    // ✅ resolve user từ JWT
    private AppUser currentUser(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            // để GlobalExceptionHandler map thành 401
            throw new SecurityException("Unauthorized");
        }

        String name = auth.getName(); // có thể là email hoặc userId tùy JWT

        // 1) thử theo email trước
        return appUserRepo.findByEmail(name)
                .orElseGet(() -> {
                    // 2) fallback: nếu name là UUID thì tìm theo id
                    try {
                        UUID id = UUID.fromString(name);
                        return appUserRepo.findById(id)
                                .orElseThrow(() -> new SecurityException("User not found"));
                    } catch (IllegalArgumentException ignore) {
                        // name không phải UUID
                        throw new SecurityException("User not found");
                    }
                });
    }

    @PostMapping("/full")
    public ResponseEntity<ApiResponse<NutritionAssessmentResponse>> createFull(
            Authentication authentication,
            @RequestBody CreateAssessmentRequest req
    ) {
        AppUser user = currentUser(authentication);

        NutritionAssessmentResponse data = assessmentService.createFullAssessment(user, req);

        return ResponseEntity.ok(ApiResponse.ok("Created assessment", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NutritionAssessmentResponse>>> list(
            @RequestParam(required = false) Boolean me,
            Authentication authentication
    ) {
        if (!Boolean.TRUE.equals(me)) {
            throw new IllegalArgumentException("me=true is required");
        }

        AppUser user = currentUser(authentication);

        List<NutritionAssessmentResponse> data = assessmentService.getMyAssessments(user);

        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public ResponseEntity<ApiResponse<NutritionAssessmentResponse>> getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        AppUser user = currentUser(authentication);

        NutritionAssessmentResponse data = assessmentService.getById(id, user);

        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
