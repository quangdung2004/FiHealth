package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NutritionAssessmentRepository extends JpaRepository<NutritionAssessment, UUID> {
    Optional<NutritionAssessment> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

}
