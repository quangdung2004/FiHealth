package com.backend.nutri_ai.assessment.service;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.dto.NutritionAssessmentResponse;
import com.backend.nutri_ai.auth.entity.AppUser;

import java.util.List;
import java.util.UUID;

public interface NutritionAssessmentService {
    NutritionAssessmentResponse createFullAssessment(AppUser user, CreateAssessmentRequest request);
    List<NutritionAssessmentResponse> getMyAssessments(AppUser user);
    NutritionAssessmentResponse getById(UUID id, AppUser user);
}
