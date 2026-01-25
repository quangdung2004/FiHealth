package com.backend.nutri_ai.catalog.mapper;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenSimpleResponse;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.FoodAllergen;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FoodMapper {

    // ===== Entity -> Response =====
    public FoodResponse toResponse(FoodItem food) {
        if (food == null) return null;

        FoodResponse response = new FoodResponse();
        response.setId(food.getId());
        response.setName(food.getName());
        response.setBrand(food.getBrand());
        response.setServingSize(food.getServingSize());
        response.setKcalPerServing(food.getKcalPerServing());
        response.setProteinG(food.getProteinG());
        response.setFatG(food.getFatG());
        response.setCarbG(food.getCarbG());
        response.setEstimatedPriceVndPerServing(food.getEstimatedPriceVndPerServing());
        response.setTags(food.getTags());
        response.setActive(food.getActive());

        response.setCreatedAt(food.getCreatedAt() != null
                ? food.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null);

        response.setUpdatedAt(food.getUpdatedAt() != null
                ? food.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null);

        response.setAllergens(mapAllergens(food.getAllergens()));

        return response;
    }

    // ===== Request -> Entity (CREATE) =====
    public FoodItem toEntity(FoodRequest request) {
        FoodItem food = new FoodItem();
        updateEntity(food, request);
        return food;
    }

    // ===== Request -> Entity (UPDATE) =====
    public void updateEntity(FoodItem food, FoodRequest request) {
        food.setName(request.getName());
        food.setBrand(request.getBrand());
        food.setServingSize(request.getServingSize());
        food.setKcalPerServing(request.getKcalPerServing());
        food.setProteinG(request.getProteinG());
        food.setFatG(request.getFatG());
        food.setCarbG(request.getCarbG());
        food.setEstimatedPriceVndPerServing(request.getEstimatedPriceVndPerServing());
        food.setTags(request.getTags());

        if (request.getActive() != null) {
            food.setActive(request.getActive());
        }
    }

    // ===== Allergen mapping =====
    private Set<AllergenSimpleResponse> mapAllergens(Set<FoodAllergen> allergens) {
        if (allergens == null) return null;

        return allergens.stream()
                .map(a -> {
                    AllergenSimpleResponse r = new AllergenSimpleResponse();
                    r.setId(a.getId());
                    r.setCode(a.getCode());
                    r.setName(a.getName());
                    return r;
                })
                .collect(Collectors.toSet());
    }
}
