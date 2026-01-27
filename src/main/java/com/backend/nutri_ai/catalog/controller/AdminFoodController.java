package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.service.IFoodService;
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
    public ResponseEntity<FoodResponse> createFood(
            @Valid @RequestBody FoodRequest request
    ) {
        return new ResponseEntity<>(
                foodService.createFood(request),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(
            @PathVariable UUID id,
            @Valid @RequestBody FoodRequest request
    ) {
        return ResponseEntity.ok(
                foodService.updateFood(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable UUID id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
