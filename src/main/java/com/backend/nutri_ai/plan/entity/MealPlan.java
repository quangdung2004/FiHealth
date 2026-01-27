package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plan", indexes = {
        @Index(name="idx_meal_plan_user", columnList="user_id"),
        @Index(name="idx_meal_plan_created", columnList="created_at")
})
@Getter @Setter
public class MealPlan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable=false, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private PlanPeriod period;

    @Column(name="start_date", nullable=false)
    private LocalDate startDate;

    @Column(name="end_date", nullable=false)
    private LocalDate endDate;

    @Column(name = "budget_per_day_vnd", nullable = false)
    private Integer budgetPerDayVnd;

    @Column(name = "estimated_total_cost_vnd", nullable = false)
    private Integer estimatedTotalCostVnd;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;



    @OneToMany(mappedBy="plan", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("date ASC")
    private List<PlanDay> days = new ArrayList<>();
}
