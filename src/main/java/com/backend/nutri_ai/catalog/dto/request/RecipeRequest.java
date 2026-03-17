package com.backend.nutri_ai.catalog.dto.request;

import com.backend.nutri_ai.common.enums.CatalogTag;
import jakarta.validation.Valid;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

@Data
public class RecipeRequest {

    @NotBlank(message = "Recipe name is required")
    @Size(max = 200, message = "Recipe name must be less than 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories must be positive")
    private Integer kcal;

    @NotNull(message = "Protein is required")
    @Min(value = 0, message = "Protein must be positive")
    private Integer proteinG;

    @NotNull(message = "Fat is required")
    @Min(value = 0, message = "Fat must be positive")
    private Integer fatG;

    @NotNull(message = "Carbohydrates are required")
    @Min(value = 0, message = "Carbohydrates must be positive")
    private Integer carbG;

    @NotNull(message = "Cost is required")
    @Min(value = 0, message = "Cost must be positive")
    private Integer estimatedCostVnd;

    @Size(max = 500, message = "Tags must be less than 500 characters")
    private String tags;

    private Set<CatalogTag> tagEnums;

    private Boolean active = true;

    @Valid
    private List<RecipeIngredientRequest> ingredients;
}
