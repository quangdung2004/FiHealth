package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="plan_meal")
@Getter
@Setter
public class PlanMeal extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="day_id", nullable=false, columnDefinition="BINARY(16)")
    private PlanDay day;

    @Column(nullable=false) private Integer mealOrder;
    @Column(nullable=false, length=50) private String name;
    @Column(nullable=false) private Integer kcal;
    @Column(nullable=false) private Integer costVnd;

    @OneToMany(mappedBy="meal", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MealItem> items = new ArrayList<>();
}

