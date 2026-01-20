package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="workout_catalog", indexes = {@Index(name="idx_workout_level", columnList="level")})
@Getter
@Setter
public class WorkoutCatalog extends BaseEntity {
    @Column(nullable=false, length=200) private String name;
    @Column(nullable=false, length=20) private String level; // BEGINNER/INTERMEDIATE...
    @Column(length=500) private String muscleGroups; // csv
    @Column(length=500) private String equipment; // csv
    @Column(length=800) private String contraindications; // csv/notes
    @Column(nullable=false) private Boolean active = true;
}

