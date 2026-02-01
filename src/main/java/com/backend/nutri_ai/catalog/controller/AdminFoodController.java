package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.service.IFoodService;
import com.backend.nutri_ai.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/foods")
@RequiredArgsConstructor
public class AdminFoodController {

    private final IFoodService foodService;

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResponse>> createFood(
            @Valid @RequestBody FoodRequest request) {
        return new ResponseEntity<>(
                ApiResponse.ok("Food created", foodService.createFood(request)),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> updateFood(
            @PathVariable UUID id,
            @Valid @RequestBody FoodRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Food updated", foodService.updateFood(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFood(@PathVariable UUID id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok(ApiResponse.ok("Food deleted", null));
    }
}
