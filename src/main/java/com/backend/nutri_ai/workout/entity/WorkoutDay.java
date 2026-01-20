package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_day",
        indexes = {
                @Index(name = "idx_workout_day_plan", columnList = "plan_id"),
                @Index(name = "ux_workout_day_plan_day", columnList = "plan_id, day_index", unique = true)
        })
@Getter
@Setter
public class WorkoutDay extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, columnDefinition = "BINARY(16)")
    private WorkoutPlan plan;

    @Column(name = "day_index", nullable = false)
    private Integer dayIndex;

    @Column(name = "title", length = 120)
    private String title;

    @Column(name = "total_duration_min")
    private Integer totalDurationMin;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemOrder ASC")
    private List<WorkoutItem> items = new ArrayList<>();
}

