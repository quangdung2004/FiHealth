package com.backend.nutri_ai.assessment.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BodyMetricsSnapshotDto {
    private Double bmi;
    private Integer bmr;
    private Integer tdee;
    private Integer calorieTarget;

    private Integer proteinG;
    private Integer fatG;
    private Integer carbG;
}
