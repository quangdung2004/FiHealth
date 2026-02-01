package com.backend.nutri_ai.assessment.mapper;

import com.backend.nutri_ai.assessment.dto.BodyMetricsSnapshotDto;
import com.backend.nutri_ai.assessment.dto.NutritionAssessmentResponse;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;

public final class NutritionAssessmentMapper {

    private NutritionAssessmentMapper() {}

    public static NutritionAssessmentResponse toResponse(NutritionAssessment a) {
        if (a == null) return null;

        return NutritionAssessmentResponse.builder()
                .id(a.getId())
                .sex(a.getSex())
                .age(a.getAge())
                .heightCm(a.getHeightCm())
                .weightKg(a.getWeightKg())
                .activityLevel(a.getActivityLevel())
                .goal(a.getGoal())
                .targetKgPerWeek(a.getTargetKgPerWeek())
                .mealsPerDay(a.getMealsPerDay())
                .budgetPerDayVnd(a.getBudgetPerDayVnd())
                .notes(a.getNotes())
                .allergies(a.getAllergies())
                .metrics(toMetricsDto(a.getMetrics()))
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private static BodyMetricsSnapshotDto toMetricsDto(BodyMetricsSnapshot m) {
        if (m == null) return null;

        return BodyMetricsSnapshotDto.builder()
                .bmi(m.getBmi())
                .bmr(m.getBmr())
                .tdee(m.getTdee())
                .calorieTarget(m.getCalorieTarget())
                .proteinG(m.getProteinG())
                .fatG(m.getFatG())
                .carbG(m.getCarbG())
                .build();
    }
}
