package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="recipe", indexes = {@Index(name="idx_recipe_name", columnList="name")})
@Getter @Setter
public class Recipe extends BaseEntity {
    @Column(nullable=false, length=200) private String name;
    @Column(length=1000) private String description;

    @Column(nullable=false) private Integer kcal;
    @Column(nullable=false) private Integer proteinG;
    @Column(nullable=false) private Integer fatG;
    @Column(nullable=false) private Integer carbG;

    @Column(nullable=false) private Integer estimatedCostVnd = 0;

    @Column(length=500) private String tags;
    @Column(nullable=false) private Boolean active = true;

    @OneToMany(mappedBy="recipe", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}

