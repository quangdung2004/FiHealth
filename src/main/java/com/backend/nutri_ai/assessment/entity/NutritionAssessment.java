package com.backend.nutri_ai.assessment.entity;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import com.backend.nutri_ai.plan.entity.MealPlan;
import com.backend.nutri_ai.workout.entity.WorkoutPlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="nutrition_assessment", indexes = {
                @Index(name="idx_assessment_user", columnList="user_id"),
                @Index(name="idx_assessment_created", columnList="created_at")
})
@Getter @Setter
public class NutritionAssessment extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    // snapshot input
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=10)
    private Sex sex;

    @Column(nullable=false) private Integer age;
    @Column(nullable=false) private Integer heightCm;
    @Column(nullable=false) private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name="activity_level", nullable=false, length=30)
    private ActivityLevel activityLevel;


    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private Goal goal;

    private Double targetKgPerWeek;

    @Column(nullable=false) private Integer mealsPerDay = 3;
    private Integer budgetPerDayVnd;

    @Column(length=500) private String notes;
    @Column(length=500) private String allergies;

    @OneToOne(mappedBy="assessment", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private BodyMetricsSnapshot metrics;

    @OneToOne(mappedBy="assessment", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private UploadedImage bodyImage;

    @OneToOne(mappedBy="assessment", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private BodyImageAnalysis bodyAnalysis;

    @OneToMany(mappedBy="assessment", fetch = FetchType.LAZY)
    private java.util.List<MealPlan> mealPlans = new java.util.ArrayList<>();

    @OneToOne(mappedBy="assessment", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private WorkoutPlan workoutPlan;
}
