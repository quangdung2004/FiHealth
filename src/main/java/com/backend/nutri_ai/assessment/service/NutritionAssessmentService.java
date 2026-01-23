package com.backend.nutri_ai.assessment.service;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;

import java.util.List;
import java.util.UUID;

public interface NutritionAssessmentService {

    NutritionAssessment createFullAssessment(AppUser user, CreateAssessmentRequest request);

    List<NutritionAssessment> getMyAssessments(AppUser user);

    NutritionAssessment getById(UUID id, AppUser user);
}
