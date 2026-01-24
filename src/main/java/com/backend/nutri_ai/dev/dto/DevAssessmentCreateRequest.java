package com.backend.nutri_ai.dev.dto;

import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevAssessmentCreateRequest {
    private Sex sex;
    private Integer age;
    private Integer heightCm;
    private Double weightKg;
    private ActivityLevel activityLevel;
    private Goal goal;
    private Integer mealsPerDay;
    private Integer budgetPerDayVnd;
}
