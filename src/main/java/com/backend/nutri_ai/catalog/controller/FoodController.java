package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.service.IFoodService;
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
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Tag(name = "Food Catalog", description = "Food catalog management APIs")
public class FoodController {

    private final IFoodService foodService;

    @Operation(summary = "Search foods")
    @GetMapping("/search")
    public ResponseEntity<Page<FoodResponse>> searchFoods(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(foodService.searchFoods(q, pageable));
    }

    @Operation(summary = "Get food by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable UUID id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }
}