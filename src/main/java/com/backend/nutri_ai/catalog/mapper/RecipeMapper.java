package com.backend.nutri_ai.catalog.mapper;

import com.backend.nutri_ai.catalog.dto.request.RecipeRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodSimpleResponse;
import com.backend.nutri_ai.catalog.dto.response.RecipeIngredientResponse;
import com.backend.nutri_ai.catalog.dto.response.RecipeResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.catalog.entity.RecipeIngredient;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    // ========= Entity -> Response =========
    public RecipeResponse toResponse(Recipe recipe) {
        if (recipe == null) return null;

        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setName(recipe.getName());
        response.setDescription(recipe.getDescription());
        response.setKcal(recipe.getKcal());
        response.setProteinG(recipe.getProteinG());
        response.setFatG(recipe.getFatG());
        response.setCarbG(recipe.getCarbG());
        response.setEstimatedCostVnd(recipe.getEstimatedCostVnd());
        response.setTags(recipe.getTags());
        response.setActive(recipe.getActive());

        response.setCreatedAt(recipe.getCreatedAt() != null
                ? recipe.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null);

        response.setUpdatedAt(recipe.getUpdatedAt() != null
                ? recipe.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null);

        if (recipe.getIngredients() != null) {
            response.setIngredients(
                    recipe.getIngredients().stream()
                            .map(this::toIngredientResponse)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    // ========= Request -> Entity =========
    public void updateEntity(Recipe recipe, RecipeRequest request) {
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setKcal(request.getKcal());
        recipe.setProteinG(request.getProteinG());
        recipe.setFatG(request.getFatG());
        recipe.setCarbG(request.getCarbG());
        recipe.setEstimatedCostVnd(request.getEstimatedCostVnd());
        recipe.setTags(request.getTags());

        if (request.getActive() != null) {
            recipe.setActive(request.getActive());
        }
    }

    // ========= Ingredient mapping =========
    private RecipeIngredientResponse toIngredientResponse(RecipeIngredient ingredient) {
        RecipeIngredientResponse response = new RecipeIngredientResponse();
        response.setId(ingredient.getId());
        response.setAmount(ingredient.getAmount());
        response.setFoodItem(toFoodSimpleResponse(ingredient.getFoodItem()));
        return response;
    }

    private FoodSimpleResponse toFoodSimpleResponse(FoodItem food) {
        if (food == null) return null;

        FoodSimpleResponse r = new FoodSimpleResponse();
        r.setId(food.getId());
        r.setName(food.getName());
        r.setServingSize(food.getServingSize());
        r.setKcalPerServing(food.getKcalPerServing());
        r.setProteinG(food.getProteinG());
        r.setFatG(food.getFatG());
        r.setCarbG(food.getCarbG());
        return r;
    }
}
