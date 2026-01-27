package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.Equipment;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_catalog", indexes = {
        @Index(name = "idx_workout_name", columnList = "name"),
        @Index(name = "idx_workout_level", columnList = "level"),
        @Index(name = "idx_workout_type", columnList = "type")
})
@Getter
@Setter
public class WorkoutCatalog extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutType type;

    @Column(length = 500)
    private String muscleGroups; // csv: "chest,triceps"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Equipment equipment;

    @Column(length = 500)
    private String contraindications; // csv: "knee_injury,back_pain"

    @Column(nullable = false)
    private Boolean active = true;
}
