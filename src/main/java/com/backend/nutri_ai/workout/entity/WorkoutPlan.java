package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plan", indexes = {
        @Index(name = "idx_plan_user", columnList = "user_id"),
        @Index(name = "idx_plan_assessment", columnList = "assessment_id")
})
@Getter
@Setter
public class WorkoutPlan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private AppUser user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false, columnDefinition = "BINARY(16)")
    private NutritionAssessment assessment;

    @Column(nullable = false)
    private Integer daysPerWeek;

    @Column(nullable = false)
    private Integer totalWeeks; // e.g. 4, 8, 12

    @Column(columnDefinition = "TEXT")
    private String aiRawJson; // Store raw AI response if needed later

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, ARCHIVED

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutDay> days = new ArrayList<>();
}
