package com.backend.nutri_ai.workout.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class WorkoutItemResponse {
    private UUID id;
    private UUID catalogId;
    private String exerciseName;
    private Integer orderIndex;
    private Integer sets;
    private Integer reps;
    private Integer durationSec;
    private Integer restSec;
    private String note;
    private Boolean completed;
}
