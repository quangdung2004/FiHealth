package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.MealType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="plan_meal", indexes = {
        @Index(name="idx_plan_meal_day", columnList="day_id")
})
@Getter @Setter
public class PlanMeal extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="day_id", nullable=false, columnDefinition="BINARY(16)")
    private PlanDay day;

    @Column(name="meal_order", nullable=false)
    private int mealOrder;

    @Enumerated(EnumType.STRING)
    @Column(name="meal_type", nullable=false, length=20)
    private MealType mealType;

    @Column(nullable=false, length=50)
    private String name;

    // ✅ THÊM 2 FIELD NÀY
    @Column(name="kcal", nullable=false)
    private int kcal = 0;

    @Column(name="cost_vnd", nullable=false)
    private int costVnd = 0;

    @OneToMany(mappedBy="meal", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<MealItem> items = new LinkedHashSet<>();

}
