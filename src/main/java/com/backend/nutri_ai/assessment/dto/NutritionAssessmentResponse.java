package com.backend.nutri_ai.assessment.dto;

import com.backend.nutri_ai.common.enums.*; // sửa theo enum package của bạn
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NutritionAssessmentResponse {
    private UUID id;

    private Sex sex;                  // đổi đúng enum của bạn
    private Integer age;
    private Integer heightCm;
    private Double weightKg;          // nếu entity là Double thì để Double, nếu Integer thì đổi
    private ActivityLevel activityLevel;
    private Goal goal;

    private Double targetKgPerWeek;
    private Integer mealsPerDay;
    private Integer budgetPerDayVnd;
    private String notes;
    private String allergies;

    private BodyMetricsSnapshotDto metrics;

    private Instant createdAt;
    private Instant updatedAt;
}
