package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_day", indexes = {
                @Index(name = "idx_day_plan", columnList = "plan_id")
})
@Getter
@Setter
public class WorkoutDay extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "plan_id", nullable = false, columnDefinition = "BINARY(16)")
        private WorkoutPlan workoutPlan;

        @Column(nullable = false)
        private Integer dayIndex; // 1..7 (or 1..totalWeeks*daysPerWeek)

        @Column(length = 50)
        private String focus; // FULL_BODY, UPPER, LOWER, CARDIO

        private Integer totalDurationMin;

        private Integer caloriesEstimate;

        @OneToMany(mappedBy = "workoutDay", cascade = CascadeType.ALL, orphanRemoval = true)
        @OrderBy("orderIndex ASC")
        private List<WorkoutItem> items = new ArrayList<>();
}
