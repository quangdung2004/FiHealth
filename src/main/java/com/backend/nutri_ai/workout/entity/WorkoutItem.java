package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_item",
        indexes = {
                @Index(name = "idx_workout_item_day", columnList = "day_id"),
                @Index(name = "idx_workout_item_catalog", columnList = "workout_catalog_id")
        })
@Getter
@Setter
public class WorkoutItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false, columnDefinition = "BINARY(16)")
    private WorkoutDay day;

    @Column(name = "item_order", nullable = false)
    private Integer itemOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_catalog_id", nullable = false, columnDefinition = "BINARY(16)")
    private WorkoutCatalog workoutCatalog;

    @Column(name = "sets_count")
    private Integer sets;

    @Column(name = "reps_per_set")
    private Integer reps;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "rest_sec")
    private Integer restSec;

    @Column(name = "intensity", length = 50)
    private String intensity;

    @Column(name = "notes", length = 500)
    private String notes;
}
