package com.backend.nutri_ai.workout.dto.request;

import com.backend.nutri_ai.common.enums.Equipment;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class WorkoutCatalogRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    private String name;

    @NotNull(message = "Level is required")
    private WorkoutLevel level;

    @NotNull(message = "Type is required")
    private WorkoutType type;

    @Size(max = 500, message = "Muscle groups must be less than 500 characters")
    private String muscleGroups;

    @NotNull(message = "Equipment is required")
    private Equipment equipment;

    @Size(max = 500, message = "Contraindications must be less than 500 characters")
    private String contraindications;

    private Boolean active = true;
}
