package com.backend.nutri_ai.catalog.mapper;

import com.backend.nutri_ai.catalog.dto.request.AllergenRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenResponse;
import com.backend.nutri_ai.catalog.entity.FoodAllergen;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AllergenMapper {

    public AllergenResponse toResponse(FoodAllergen allergen) {
        AllergenResponse response = new AllergenResponse();
        response.setId(allergen.getId());
        response.setCode(allergen.getCode());
        response.setName(allergen.getName());
        response.setCreatedAt(
                allergen.getCreatedAt() != null
                        ? allergen.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null
        );
        response.setUpdatedAt(
                allergen.getUpdatedAt() != null
                        ? allergen.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null
        );
        return response;
    }

    public void updateEntity(FoodAllergen allergen, AllergenRequest request) {
        allergen.setCode(request.getCode().toUpperCase().trim());
        allergen.setName(request.getName().trim());
    }
}
