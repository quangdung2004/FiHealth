package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.RecipeIngredientRequest;
import com.backend.nutri_ai.catalog.dto.request.RecipeRequest;
import com.backend.nutri_ai.catalog.dto.response.RecipeResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.entity.Recipe;
import com.backend.nutri_ai.catalog.entity.RecipeIngredient;
import com.backend.nutri_ai.catalog.mapper.RecipeMapper;
import com.backend.nutri_ai.catalog.repository.IFoodRepository;
import com.backend.nutri_ai.catalog.repository.IRecipeRepository;
import com.backend.nutri_ai.common.exception.InvalidRequestException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeServicelmp implements IRecipeService {

    private final IRecipeRepository recipeRepository;
    private final IFoodRepository foodRepository;
    private final RecipeMapper recipeMapper;

    // ================= SEARCH =================
    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(String query, Pageable pageable) {
        Page<Recipe> recipes;

        if (query == null || query.trim().isEmpty()) {
            recipes = recipeRepository.findAll(pageable);
        } else {
            recipes = recipeRepository.searchRecipes(query.trim(), true, pageable);
        }

        return recipes.map(recipeMapper::toResponse);
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        return recipeMapper.toResponse(recipe);
    }

    // ================= CREATE =================
    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        validateRecipeRequest(request);

        Recipe recipe = new Recipe();
        recipeMapper.updateEntity(recipe, request);

        if (request.getIngredients() != null) {
            request.getIngredients()
                    .forEach(req -> addIngredient(recipe, req));
        }

        recipeRepository.save(recipe);
        return recipeMapper.toResponse(recipe);
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public RecipeResponse updateRecipe(UUID id, RecipeRequest request) {
        validateRecipeRequest(request);

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        recipe.getIngredients().clear();
        recipeMapper.updateEntity(recipe, request);

        if (request.getIngredients() != null) {
            request.getIngredients()
                    .forEach(req -> addIngredient(recipe, req));
        }

        recipeRepository.save(recipe); //
        return recipeMapper.toResponse(recipe);
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        recipe.setActive(false);
        recipeRepository.save(recipe);
    }

    // ================= BUSINESS LOGIC =================
    private void addIngredient(Recipe recipe, RecipeIngredientRequest req) {
        UUID foodId;
        try {
            foodId = UUID.fromString(req.getFoodItemId());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid food item ID: " + req.getFoodItemId());
        }

        FoodItem food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + req.getFoodItemId()));

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setRecipe(recipe);
        ingredient.setFoodItem(food);
        ingredient.setAmount(req.getAmount());

        recipe.getIngredients().add(ingredient);
    }

    private void validateRecipeRequest(RecipeRequest request) {
        if (request.getKcal() != null && request.getKcal() < 0)
            throw new InvalidRequestException("Calories cannot be negative");

        if (request.getProteinG() != null && request.getProteinG() < 0)
            throw new InvalidRequestException("Protein cannot be negative");

        if (request.getFatG() != null && request.getFatG() < 0)
            throw new InvalidRequestException("Fat cannot be negative");

        if (request.getCarbG() != null && request.getCarbG() < 0)
            throw new InvalidRequestException("Carbohydrates cannot be negative");

        if (request.getEstimatedCostVnd() != null && request.getEstimatedCostVnd() < 0)
            throw new InvalidRequestException("Cost cannot be negative");
    }
}
