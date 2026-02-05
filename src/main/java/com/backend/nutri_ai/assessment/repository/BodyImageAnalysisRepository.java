package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BodyImageAnalysisRepository extends JpaRepository<BodyImageAnalysis, UUID> {

    @Query("""
        select a
        from BodyImageAnalysis a
        join a.assessment ass
        join ass.user u
        where ass.id = :assessmentId
          and u.id = :userId
    """)
    Optional<BodyImageAnalysis> findByAssessmentIdAndUserId(
            @Param("assessmentId") UUID assessmentId,
            @Param("userId") UUID userId
    );
}
