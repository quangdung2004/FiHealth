package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.RecipeIngredientRequest;
import com.backend.nutri_ai.catalog.dto.request.RecipeRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodSimpleResponse;
import com.backend.nutri_ai.catalog.dto.response.RecipeIngredientResponse;
import com.backend.nutri_ai.catalog.dto.response.RecipeResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.catalog.entity.RecipeIngredient;
import com.backend.nutri_ai.catalog.repository.IFoodRepository;
import com.backend.nutri_ai.catalog.repository.IRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServicelmp implements IRecipeService {

    private final IRecipeRepository recipeRepository;
    private final IFoodRepository foodRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(String query, Pageable pageable) {
        Page<Recipe> recipes;

        if (query == null || query.trim().isEmpty()) {
            recipes = recipeRepository.findAll(pageable);
        } else {
            recipes = recipeRepository.searchRecipes(query.trim(), true, pageable);
        }

        return recipes.map(this::mapToRecipeResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
        return mapToRecipeResponse(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        // Validate
        validateRecipeRequest(request);

        Recipe recipe = new Recipe();
        mapRequestToRecipe(request, recipe);

        // Add ingredients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            for (RecipeIngredientRequest ingredientReq : request.getIngredients()) {
                addIngredientToRecipe(recipe, ingredientReq);
            }
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        return mapToRecipeResponse(savedRecipe);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(UUID id, RecipeRequest request) {
        // Validate
        validateRecipeRequest(request);

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        // Clear existing ingredients
        recipe.getIngredients().clear();

        mapRequestToRecipe(request, recipe);

        // Add updated ingredients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            for (RecipeIngredientRequest ingredientReq : request.getIngredients()) {
                addIngredientToRecipe(recipe, ingredientReq);
            }
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return mapToRecipeResponse(updatedRecipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
        recipe.setActive(false);
        recipeRepository.save(recipe);
    }

    private void addIngredientToRecipe(Recipe recipe, RecipeIngredientRequest ingredientReq) {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setRecipe(recipe);

        UUID foodItemId;
        try {
            foodItemId = UUID.fromString(ingredientReq.getFoodItemId());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid food item ID format: " + ingredientReq.getFoodItemId());
        }

        FoodItem foodItem = foodRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found: " + ingredientReq.getFoodItemId()));
        ingredient.setFoodItem(foodItem);

        ingredient.setAmount(ingredientReq.getAmount());
        recipe.getIngredients().add(ingredient);
    }

    private void validateRecipeRequest(RecipeRequest request) {
        if (request.getKcal() != null && request.getKcal() < 0) {
            throw new RuntimeException("Calories cannot be negative");
        }
        if (request.getProteinG() != null && request.getProteinG() < 0) {
            throw new RuntimeException("Protein cannot be negative");
        }
        if (request.getFatG() != null && request.getFatG() < 0) {
            throw new RuntimeException("Fat cannot be negative");
        }
        if (request.getCarbG() != null && request.getCarbG() < 0) {
            throw new RuntimeException("Carbohydrates cannot be negative");
        }
        if (request.getEstimatedCostVnd() != null && request.getEstimatedCostVnd() < 0) {
            throw new RuntimeException("Cost cannot be negative");
        }
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe) {
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
        response.setCreatedAt(recipe.getCreatedAt() != null ?
                recipe.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
        response.setUpdatedAt(recipe.getUpdatedAt() != null ?
                recipe.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);

        // Map ingredients
        if (recipe.getIngredients() != null) {
            response.setIngredients(recipe.getIngredients().stream()
                    .map(this::mapToIngredientResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private RecipeIngredientResponse mapToIngredientResponse(RecipeIngredient ingredient) {
        RecipeIngredientResponse response = new RecipeIngredientResponse();
        response.setId(ingredient.getId());
        response.setAmount(ingredient.getAmount());

        // Map food item
        FoodSimpleResponse foodResponse = new FoodSimpleResponse();
        FoodItem food = ingredient.getFoodItem();
        foodResponse.setId(food.getId());
        foodResponse.setName(food.getName());
        foodResponse.setServingSize(food.getServingSize());
        foodResponse.setKcalPerServing(food.getKcalPerServing());
        foodResponse.setProteinG(food.getProteinG());
        foodResponse.setFatG(food.getFatG());
        foodResponse.setCarbG(food.getCarbG());

        response.setFoodItem(foodResponse);
        return response;
    }

    private void mapRequestToRecipe(RecipeRequest request, Recipe recipe) {
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
}