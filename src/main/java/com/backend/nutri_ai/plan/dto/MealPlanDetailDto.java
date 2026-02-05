package com.backend.nutri_ai.plan.dto;

import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.enums.Goal;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MealPlanDetailDto {
    private UUID id;

    private PlanPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;

    private Integer totalDays;
    private Integer budgetPerDayVnd;
    private Integer estimatedTotalCostVnd;

    private boolean favorite;

    private Instant createdAt;
    private Instant updatedAt;

    private UUID assessmentId;
    private Goal goal;
}
