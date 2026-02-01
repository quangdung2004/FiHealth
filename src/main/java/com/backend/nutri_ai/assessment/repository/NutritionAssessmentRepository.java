package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NutritionAssessmentRepository extends JpaRepository<NutritionAssessment, UUID> {
    Optional<NutritionAssessment> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("""
        select a from NutritionAssessment a
        left join fetch a.metrics m
        where a.id = :id
    """)
    Optional<NutritionAssessment> findByIdWithMetrics(@Param("id") UUID id);

    @Query("""
 select a from NutritionAssessment a
 left join fetch a.metrics m
 where a.id = :id and a.user.id = :userId
""")
    Optional<NutritionAssessment> findByIdWithMetricsAndUserId(UUID id, UUID userId);

}
