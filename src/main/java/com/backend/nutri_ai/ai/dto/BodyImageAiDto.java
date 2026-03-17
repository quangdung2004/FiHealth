package com.backend.nutri_ai.ai.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BodyImageAiDto {
    private Double bodyFatMin;
    private Double bodyFatMax;
    private String postureNotes;
    private String proportionsNotes;
    private String safetyNotes;
}
