package com.backend.nutri_ai.catalog.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class RecipeIngredientResponse {
    private UUID id;
    private FoodSimpleResponse foodItem;
    private String amount;
}