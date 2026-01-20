package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="template_item")
@Getter
@Setter
public class TemplateItem extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="meal_id", nullable=false, columnDefinition="BINARY(16)")
    private TemplateMeal meal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id", columnDefinition="BINARY(16)")
    private Recipe recipe; // ưu tiên recipe

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="food_item_id", columnDefinition="BINARY(16)")
    private FoodItem foodItem; // hoặc food item

    @Column(length=50)
    private String amountOverride;
}

