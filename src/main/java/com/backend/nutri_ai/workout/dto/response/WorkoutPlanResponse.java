package com.backend.nutri_ai.workout.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WorkoutPlanResponse {
    private UUID id;
    private UUID assessmentId;
    private Integer daysPerWeek;
    private Integer totalWeeks;
    private String status;
    private List<WorkoutDayResponse> days;
}
