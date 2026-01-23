package com.backend.nutri_ai.assessment.repo;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NutritionAssessmentRepo extends JpaRepository<NutritionAssessment, UUID> {

    List<NutritionAssessment> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    Optional<NutritionAssessment> findByIdAndUser_Id(UUID id, UUID userId);
}