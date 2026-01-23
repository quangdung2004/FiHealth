package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, UUID> {

}
