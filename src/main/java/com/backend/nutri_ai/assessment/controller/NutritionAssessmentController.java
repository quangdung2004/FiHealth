package com.backend.nutri_ai.assessment.controller;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.service.NutritionAssessmentService;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repo.AppUserRepo;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class NutritionAssessmentController {

    private final NutritionAssessmentService assessmentService;
    private final AppUserRepository appUserRepo;

    // ✅ hàm chung để resolve user từ JWT
    private AppUser currentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String name = auth.getName(); // có thể là email hoặc userId tùy bạn build JWT

        // 1) thử theo email trước
        var byEmail = appUserRepo.findByEmail(name);
        if (byEmail.isPresent()) return byEmail.get();

        // 2) fallback: nếu name là UUID thì tìm theo id
        try {
            UUID id = UUID.fromString(name);
            return appUserRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        } catch (IllegalArgumentException ignore) {
            // name không phải UUID
        }

        // 3) nếu JWT bạn set "sub" = username thì cần findByUsername (nếu có)
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
    }

    @PostMapping("/full")
    public NutritionAssessment createFull(
            Authentication authentication,
            @RequestBody CreateAssessmentRequest req
    ) {
        String username = principal.getName(); // user đang login

        AppUser user = appUserRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AppUser user = currentUser(authentication);
        return assessmentService.createFullAssessment(user, req);
    }

    @GetMapping
    public List<NutritionAssessment> list(
            @RequestParam(required = false) Boolean me,
            Authentication authentication
    ) {
        if (!Boolean.TRUE.equals(me)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "me=true is required");
        }

        AppUser user = currentUser(authentication);
        return assessmentService.getMyAssessments(user);
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public NutritionAssessment getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        AppUser user = currentUser(authentication);
        return assessmentService.getById(id, user);
    }
}



