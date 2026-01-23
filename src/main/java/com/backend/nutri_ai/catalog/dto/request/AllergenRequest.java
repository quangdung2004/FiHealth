package com.backend.nutri_ai.catalog.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class AllergenRequest {

    @NotBlank(message = "Allergen code is required")
    @Size(max = 40, message = "Code must be less than 40 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Code must be uppercase with underscores")
    private String code;

    @NotBlank(message = "Allergen name is required")
    @Size(max = 120, message = "Name must be less than 120 characters")
    private String name;
}