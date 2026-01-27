package com.backend.nutri_ai.workout.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WorkoutDayResponse {
    private UUID id;
    private Integer dayIndex;
    private String focus;
    private Integer totalDurationMin;
    private Integer caloriesEstimate;
    private List<WorkoutItemResponse> items;
}
