package com.backend.nutri_ai.assessment.controller;

import com.backend.nutri_ai.assessment.dto.BodyAnalysisResponse;
import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import com.backend.nutri_ai.assessment.mapper.BodyImageAnalysisResponseMapper;
import com.backend.nutri_ai.assessment.repository.BodyImageAnalysisRepository;
import com.backend.nutri_ai.assessment.service.BodyImageAnalysisService;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.exception.BadRequestException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@Slf4j
public class BodyImageController {

    private final BodyImageAnalysisService service;
    private final BodyImageAnalysisRepository analysisRepo;
    private final BodyImageAnalysisResponseMapper mapper;

    @PostMapping(
            value = "/{id}/body-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponse<BodyAnalysisResponse> upload(
            @PathVariable UUID id,
            @RequestParam("image") MultipartFile image
    ) throws Exception {

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("IMAGE_REQUIRED", "Image file is required");
        }

        log.info("Upload body image: assessmentId={} filename={} sizeBytes={}",
                id, image.getOriginalFilename(), image.getSize());

        BodyImageAnalysis analysis = service.analyze(id, image);
        return ApiResponse.ok("Body image analyzed", mapper.toResponse(analysis));
    }

    @GetMapping(value = "/{id}/body-analysis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<BodyAnalysisResponse> get(@PathVariable UUID id) {

        BodyImageAnalysis analysis = analysisRepo.findByAssessmentId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BODY_ANALYSIS_NOT_FOUND",
                        "Body analysis not found for assessmentId=" + id
                ));

        log.info("Get body analysis: assessmentId={}", id);
        return ApiResponse.ok(mapper.toResponse(analysis));
    }
}
