package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_item", indexes = {
                @Index(name = "idx_item_day", columnList = "day_id"),
                @Index(name = "idx_item_catalog", columnList = "catalog_id")
})
@Getter
@Setter
public class WorkoutItem extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "day_id", nullable = false, columnDefinition = "BINARY(16)")
        private WorkoutDay workoutDay;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "catalog_id", nullable = false, columnDefinition = "BINARY(16)")
        private WorkoutCatalog workoutCatalog;

        @Column(nullable = false)
        private Integer orderIndex; // To sort exercises within a day

        private Integer sets;
        private Integer reps; // e.g. 10 or 12
        private Integer durationSec; // e.g. 60 (for plank)
        private Integer restSec;

        @Column(length = 500)
        private String note; // Form cues or specific instructions

        private Boolean completed = false;
}
