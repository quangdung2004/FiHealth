package com.backend.nutri_ai.catalog.dto.response;

import com.backend.nutri_ai.common.enums.CatalogTag;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class FoodResponse {
    private UUID id;
    private String name;
    private String brand;
    private String servingSize;
    private Integer kcalPerServing;
    private Integer proteinG;
    private Integer fatG;
    private Integer carbG;
    private Integer estimatedPriceVndPerServing;
    private String tags;
    private Set<CatalogTag> tagEnums;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<AllergenSimpleResponse> allergens;
}
