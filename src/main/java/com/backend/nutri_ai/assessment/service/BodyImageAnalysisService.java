package com.backend.nutri_ai.assessment.service;

import com.backend.nutri_ai.ai.service.BodyImageAiService;
import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.mapper.BodyImageAnalysisMapper;
import com.backend.nutri_ai.assessment.mapper.UploadedImageMapper;
import com.backend.nutri_ai.assessment.repository.BodyImageAnalysisRepository;
import com.backend.nutri_ai.assessment.repository.NutritionAssessmentRepository;
import com.backend.nutri_ai.assessment.repository.UploadedImageRepository;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.exception.AiResponseInvalidException;
import com.backend.nutri_ai.common.exception.ImageRequiredException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.common.security.DevUserResolver;
import com.backend.nutri_ai.common.storage.LocalStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyImageAnalysisService {

    private final DevUserResolver userResolver;
    private final NutritionAssessmentRepository assessmentRepo;
    private final UploadedImageRepository imageRepo;
    private final BodyImageAnalysisRepository analysisRepo;
    private final LocalStorageService storage;
    private final BodyImageAiService aiService;
    private final UploadedImageMapper uploadedImageMapper;
    private final BodyImageAnalysisMapper analysisMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public BodyImageAnalysis analyze(UUID assessmentId, MultipartFile image) {
        // 1) validate image
        if (image == null || image.isEmpty()) {
            throw new ImageRequiredException("Please upload image!"); // -> ErrorCode.IMAGE_REQUIRED
        }

        AppUser user = userResolver.getCurrentUser();

        // 2) validate assessment exists
        NutritionAssessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment not found: " + assessmentId
                ));

        // 3) save storage
        var stored = storage.save(assessmentId, image);

        // 4) persist uploaded image
        var uploaded = imageRepo.findByAssessmentId(assessment.getId())
                .orElseGet(() ->
                        uploadedImageMapper.toNewEntity(user, assessment, stored)
                );

        // nếu đã tồn tại thì update
        uploadedImageMapper.update(uploaded, user, assessment, stored);

        imageRepo.save(uploaded);


        // 5) call AI
        var ai = aiService.analyze(user, assessment, image);

        // 6) upsert analysis
        BodyImageAnalysis analysis = analysisRepo.findByAssessmentId(assessmentId)
                .orElseGet(BodyImageAnalysis::new);

        try {
            String rawJson = objectMapper.writeValueAsString(ai);
            analysisMapper.update(analysis, assessment, ai, rawJson);
        } catch (Exception e) {
            throw new AiResponseInvalidException(
                    "AI response invalid for assessmentId=" + assessmentId
            );
        }

        BodyImageAnalysis saved = analysisRepo.save(analysis);
        log.info("Body image analysis saved. assessmentId={}", assessmentId);
        return saved;
    }

    @Transactional(readOnly = true)
    public BodyImageAnalysis getByAssessmentId(UUID assessmentId) {
        return analysisRepo.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Body analysis not found for assessmentId"
                ));
    }
}
