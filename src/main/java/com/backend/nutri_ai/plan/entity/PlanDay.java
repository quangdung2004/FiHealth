package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="plan_day")
@Getter
@Setter
public class PlanDay extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable=false, columnDefinition="BINARY(16)")
    private MealPlan plan;

    @Column(nullable=false) private Integer dayIndex;
    @Column(nullable=false) private Integer totalKcal;
    @Column(nullable=false) private Integer costVnd;

    @OneToMany(mappedBy="day", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<PlanMeal> meals = new ArrayList<>();
}

