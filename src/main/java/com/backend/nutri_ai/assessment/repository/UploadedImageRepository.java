package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadedImageRepository extends JpaRepository<UploadedImage, UUID> {

    @Query("""
        select ui
        from UploadedImage ui
        join ui.assessment ass
        join ass.user u
        where ass.id = :assessmentId
          and u.id = :userId
    """)
    Optional<UploadedImage> findByAssessmentIdAndUserId(
            @Param("assessmentId") UUID assessmentId,
            @Param("userId") UUID userId
    );
}
