package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="meal_item")
@Getter
@Setter
public class MealItem extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="meal_id", nullable=false, columnDefinition="BINARY(16)")
    private PlanMeal meal;

    @Column(nullable=false, length=200) private String foodName;
    @Column(nullable=false, length=50) private String amount;

    @Column(nullable=false) private Integer kcal;
    @Column(nullable=false) private Integer costVnd;

    private Integer proteinG;
    private Integer fatG;
    private Integer carbG;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="food_item_id", columnDefinition="BINARY(16)")
    private FoodItem foodItem;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id", columnDefinition="BINARY(16)")
    private Recipe recipe;
}

