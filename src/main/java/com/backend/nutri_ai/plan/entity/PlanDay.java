package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="plan_day", indexes = {
        @Index(name="idx_plan_day_plan", columnList="plan_id")
})
@Getter @Setter
public class PlanDay extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable=false, columnDefinition="BINARY(16)")
    private MealPlan plan;

    @Column(nullable=false)
    private LocalDate date;

    @Column(name="day_index", nullable=false)
    private int dayIndex;

    @Column(name = "cost_vnd", nullable = false)
    private Integer costVnd = 0;

    @Column(name = "total_kcal", nullable = false)
    private Integer totalKcal;


    @OneToMany(mappedBy="day", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("mealOrder ASC")
    private List<PlanMeal> meals = new ArrayList<>();
}
