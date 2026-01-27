package com.backend.nutri_ai.assessment.controller;

import com.backend.nutri_ai.assessment.dto.BodyAnalysisResponse;
import com.backend.nutri_ai.assessment.mapper.BodyImageAnalysisResponseMapper;
import com.backend.nutri_ai.assessment.service.BodyImageAnalysisService;
import com.backend.nutri_ai.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@Slf4j
public class BodyImageController {

    private final BodyImageAnalysisService service;
    private final BodyImageAnalysisResponseMapper mapper;

    @PostMapping(
            value = "/{id}/body-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<BodyAnalysisResponse>> upload(
            @PathVariable UUID id,
            @RequestParam("image") MultipartFile image
    ) {
        log.info("Upload body image: assessmentId={} filename={} sizeBytes={}",
                id,
                image != null ? image.getOriginalFilename() : null,
                image != null ? image.getSize() : null
        );

        var analysis = service.analyze(id, image);

        return ResponseEntity.ok(
                ApiResponse.ok("Body image analyzed", mapper.toResponse(analysis))
        );
    }

    @GetMapping(value = "/{id}/body-analysis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BodyAnalysisResponse>> get(@PathVariable UUID id) {

        log.info("Get body analysis: assessmentId={}", id);

        var analysis = service.getByAssessmentId(id);

        return ResponseEntity.ok(
                ApiResponse.ok(mapper.toResponse(analysis))
        );
    }
}
