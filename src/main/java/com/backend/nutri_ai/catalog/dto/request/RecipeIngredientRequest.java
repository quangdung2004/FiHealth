package com.backend.nutri_ai.catalog.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RecipeIngredientRequest {

    @NotBlank(message = "Food item ID is required")
    private String foodItemId;

    @NotBlank(message = "Amount is required")
    @Size(max = 50, message = "Amount must be less than 50 characters")
    private String amount;
}