package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="meal_plan", indexes = {
        @Index(name="idx_plan_user", columnList="user_id")
})
@Getter
@Setter
public class MealPlan extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assessment_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private PlanPeriod period; // DAY/WEEK/MONTH

    @Column(nullable=false)
    private Integer totalDays;

    @Column(nullable=false)
    private Integer budgetPerDayVnd;

    @Column(nullable=false)
    private Integer estimatedTotalCostVnd;

    @Lob
    @Column(columnDefinition="TEXT")
    private String aiRawJson;

    @OneToMany(mappedBy="plan", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<PlanDay> days = new ArrayList<>();
}

