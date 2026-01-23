package com.backend.nutri_ai.assessment.dto;

import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAssessmentRequest {

    private Sex sex;
    private Integer age;
    private Integer heightCm;
    private Double weightKg;

    private ActivityLevel activityLevel;
    private Goal goal;

    private Double targetKgPerWeek;

    private Integer mealsPerDay;
    private Integer budgetPerDayVnd;

    private String allergies;
    private String notes;
}
