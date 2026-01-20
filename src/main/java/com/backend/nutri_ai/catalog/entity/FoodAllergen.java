package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="food_allergen", indexes = {
        @Index(name="ux_allergen_code", columnList="code", unique=true)
})
@Getter @Setter
public class FoodAllergen extends BaseEntity {

    @Column(nullable=false, length=40) private String code; // PEANUT, MILK...

    @Column(nullable=false, length=120) private String name;

    @ManyToMany(mappedBy = "allergens")
    private Set<FoodItem> foodItems = new HashSet<>();
}

