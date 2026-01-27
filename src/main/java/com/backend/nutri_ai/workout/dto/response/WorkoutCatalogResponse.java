package com.backend.nutri_ai.workout.dto.response;

import com.backend.nutri_ai.common.enums.Equipment;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import lombok.Data;

import java.util.UUID;

@Data
public class WorkoutCatalogResponse {
    private UUID id;
    private String name;
    private WorkoutLevel level;
    private WorkoutType type;
    private String muscleGroups;
    private Equipment equipment;
    private String contraindications;
    private Boolean active;
}
