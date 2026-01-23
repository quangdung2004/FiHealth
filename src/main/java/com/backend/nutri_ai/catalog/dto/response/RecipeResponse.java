package com.backend.nutri_ai.catalog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RecipeResponse {
    private UUID id;
    private String name;
    private String description;
    private Integer kcal;
    private Integer proteinG;
    private Integer fatG;
    private Integer carbG;
    private Integer estimatedCostVnd;
    private String tags;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<RecipeIngredientResponse> ingredients;
}



