package com.backend.nutri_ai.assessment.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BodyAnalysisResponse {
    private UUID assessmentId;
    private Double bodyFatMin;
    private Double bodyFatMax;
    private String postureNotes;
    private String proportionsNotes;
    private String safetyNotes;
}
