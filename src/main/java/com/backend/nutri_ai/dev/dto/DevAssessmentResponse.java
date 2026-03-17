package com.backend.nutri_ai.dev.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevAssessmentResponse {
    private UUID assessmentId;
}
