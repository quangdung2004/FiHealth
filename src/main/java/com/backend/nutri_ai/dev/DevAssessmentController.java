package com.backend.nutri_ai.dev;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.dev.dto.DevAssessmentCreateRequest;
import com.backend.nutri_ai.dev.dto.DevAssessmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev/assessments")
@RequiredArgsConstructor
@Profile({"dev", "default"}) // chỉ mở trong dev/default
public class DevAssessmentController {

    private final DevAssessmentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DevAssessmentResponse>> create(
            @RequestBody(required = false) DevAssessmentCreateRequest req
    ) {
        NutritionAssessment a = service.createForDevUser(req);
        return ResponseEntity.ok(ApiResponse.ok(
                "Dev assessment created",
                DevAssessmentResponse.builder().assessmentId(a.getId()).build()
        ));
    }


    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<DevAssessmentResponse>> latest() {
        NutritionAssessment a = service.latestForDevUserOrNull();
        if (a == null) {
            throw new ResourceNotFoundException(
                    "No assessment found for DEV user");
        }
        return ResponseEntity.ok(ApiResponse.ok(
                DevAssessmentResponse.builder()
                        .assessmentId(a.getId())
                        .build()
        ));
    }
}
