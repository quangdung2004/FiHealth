package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.RecipeRequest;
import com.backend.nutri_ai.catalog.dto.response.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IRecipeService {

    Page<RecipeResponse> searchRecipes(String query, Pageable pageable);

    RecipeResponse getRecipeById(UUID id);

    RecipeResponse createRecipe(RecipeRequest request);

    RecipeResponse updateRecipe(UUID id, RecipeRequest request);

    void deleteRecipe(UUID id);
}