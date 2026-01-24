package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadedImageRepository extends JpaRepository<UploadedImage, UUID> {
    Optional<UploadedImage> findByAssessmentId(UUID assessmentId);
}
