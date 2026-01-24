package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface BodyImageAnalysisRepository extends JpaRepository<BodyImageAnalysis, UUID> {
    Optional<BodyImageAnalysis> findByAssessmentId(UUID assessmentId);
}
