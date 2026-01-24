package com.backend.nutri_ai.dev;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.dev.dto.DevAssessmentCreateRequest;
import com.backend.nutri_ai.dev.dto.DevAssessmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dev/assessments")
@RequiredArgsConstructor
@Profile({"dev", "default"}) // chỉ mở trong dev/default
public class DevAssessmentController {

    private final DevAssessmentService service;

    @PostMapping
    public ApiResponse<DevAssessmentResponse> create(
            @RequestBody(required = false) DevAssessmentCreateRequest req
    ) {
        NutritionAssessment a = service.createForDevUser(req);
        return ApiResponse.ok("Dev assessment created",
                DevAssessmentResponse.builder().assessmentId(a.getId()).build()
        );
    }


    @GetMapping("/latest")
    public ApiResponse<DevAssessmentResponse> latest() {
        NutritionAssessment a = service.latestForDevUserOrNull();
        if (a == null) {
            throw new ResourceNotFoundException("DEV_ASSESSMENT_NOT_FOUND", "No dev assessment found");
        }
        return ApiResponse.ok(
                DevAssessmentResponse.builder()
                        .assessmentId(a.getId())
                        .build()
        );
    }
}
