package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="food_item", indexes = {
        @Index(name="idx_food_name", columnList="name")
})
@Getter @Setter
public class FoodItem extends BaseEntity {

    @Column(nullable=false, length=200)
    private String name;

    @Column(length=120)
    private String brand;

    @Column(length=50)
    private String servingSize; // "100g", "1 quả"

    @Column(nullable=false) private Integer kcalPerServing;
    @Column(nullable=false) private Integer proteinG;
    @Column(nullable=false) private Integer fatG;
    @Column(nullable=false) private Integer carbG;

    @Column(nullable=false) private Integer estimatedPriceVndPerServing = 0;

    @Column(length=500)
    private String tags; // csv: "lowcarb,highprotein"

    @Column(nullable=false)
    private Boolean active = true;

    @ManyToMany
    @JoinTable(name="food_item_allergen",
            joinColumns=@JoinColumn(name="food_item_id", columnDefinition="BINARY(16)"),
            inverseJoinColumns=@JoinColumn(name="allergen_id", columnDefinition="BINARY(16)")
    )
    private Set<FoodAllergen> allergens = new HashSet<>();

}
