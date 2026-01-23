package com.backend.nutri_ai.catalog.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.UUID;

@Data
public class FoodRequest {

    @NotBlank(message = "Food name is required")
    @Size(max = 200, message = "Food name must be less than 200 characters")
    private String name;

    @Size(max = 120, message = "Brand must be less than 120 characters")
    private String brand;

    @Size(max = 50, message = "Serving size must be less than 50 characters")
    private String servingSize;

    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories must be positive")
    private Integer kcalPerServing;

    @NotNull(message = "Protein is required")
    @Min(value = 0, message = "Protein must be positive")
    private Integer proteinG;

    @NotNull(message = "Fat is required")
    @Min(value = 0, message = "Fat must be positive")
    private Integer fatG;

    @NotNull(message = "Carbohydrates are required")
    @Min(value = 0, message = "Carbohydrates must be positive")
    private Integer carbG;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Integer estimatedPriceVndPerServing;

    @Size(max = 500, message = "Tags must be less than 500 characters")
    private String tags;

    private Boolean active = true;

    private Set<UUID> allergenIds;
}