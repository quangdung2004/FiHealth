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
import com.backend.nutri_ai.common.exception.BadRequestException;
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
    public BodyImageAnalysis analyze(UUID assessmentId, MultipartFile image) throws Exception {

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("IMAGE_REQUIRED", "Image file is required");
        }

        AppUser user = userResolver.getCurrentUser();

        NutritionAssessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ASSESSMENT_NOT_FOUND",
                        "Assessment not found: " + assessmentId
                ));

        var stored = storage.save(assessmentId, image);

        // map UploadedImage (builder DTO + mapper)
        var uploaded = uploadedImageMapper.toEntity(user, assessment, stored);
        imageRepo.save(uploaded);

        // call AI
        var ai = aiService.analyze(user, assessment, image);

        // upsert analysis + mapper update
        BodyImageAnalysis analysis = analysisRepo.findByAssessmentId(assessmentId)
                .orElseGet(BodyImageAnalysis::new);

        String rawJson = objectMapper.writeValueAsString(ai);
        analysisMapper.update(analysis, assessment, ai, rawJson);

        BodyImageAnalysis saved = analysisRepo.save(analysis);
        log.info("Body image analysis saved. assessmentId={}", assessmentId);
        return saved;
    }
}
