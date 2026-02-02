package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.catalog.dto.request.RecipeRequest;
import com.backend.nutri_ai.catalog.dto.response.RecipeResponse;
import com.backend.nutri_ai.catalog.service.IRecipeService;
import com.backend.nutri_ai.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/recipes")
@RequiredArgsConstructor
@Tag(name = "Admin Recipe Management", description = "Admin recipe CRUD operations")
public class AdminRecipeController {

    private final IRecipeService recipeService;

    @Operation(summary = "Get all recipes (admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getAllRecipes(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recipeService.searchRecipes(q, pageable)));
    }

    @Operation(summary = "Get recipe by ID (admin)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(recipeService.getRecipeById(id)));
    }

    @Operation(summary = "Create new recipe")
    @PostMapping
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(@Valid @RequestBody RecipeRequest request) {
        return new ResponseEntity<>(
                ApiResponse.ok("Recipe created", recipeService.createRecipe(request)),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update recipe")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Recipe updated", recipeService.updateRecipe(id, request)));
    }

    @Operation(summary = "Delete recipe (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(ApiResponse.ok("Recipe deleted", null));
    }
}