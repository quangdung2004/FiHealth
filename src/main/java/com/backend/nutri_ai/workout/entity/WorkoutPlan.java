package com.backend.nutri_ai.workout.entity;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="workout_plan", indexes = {@Index(name="idx_workoutplan_user", columnList="user_id")})
@Getter
@Setter
public class WorkoutPlan extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assessment_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    @Column(nullable=false) private Integer daysPerWeek;

    @Lob @Column(columnDefinition="TEXT")
    private String aiRawJson;

    @OneToMany(mappedBy="plan", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<WorkoutDay> days = new ArrayList<>();
}

