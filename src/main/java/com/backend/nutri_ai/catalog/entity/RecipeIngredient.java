package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="recipe_ingredient")
@Getter
@Setter
public class RecipeIngredient extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id", nullable=false, columnDefinition="BINARY(16)")
    private Recipe recipe;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="food_item_id", nullable=false, columnDefinition="BINARY(16)")
    private FoodItem foodItem;

    @Column(nullable=false, length=50)
    private String amount; // "150g", "1 muỗng"
}
