package com.backend.nutri_ai.workout.controller;

import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.dto.request.WorkoutCatalogRequest;
import com.backend.nutri_ai.workout.dto.response.WorkoutCatalogResponse;
import com.backend.nutri_ai.workout.service.IWorkoutService;
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
@RequestMapping("/api/admin/workouts")
@RequiredArgsConstructor
@Tag(name = "Admin Workout Catalog", description = "Admin APIs for managing workout catalog")
public class AdminWorkoutController {

    private final IWorkoutService workoutService;

    @Operation(summary = "Search workout catalog")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkoutCatalogResponse>>> searchCatalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) WorkoutLevel level,
            @RequestParam(required = false) WorkoutType type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.searchCatalog(q, active, level, type, pageable)));
    }

    @Operation(summary = "Create workout item")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkoutCatalogResponse>> createWorkout(
            @Valid @RequestBody WorkoutCatalogRequest request) {
        return new ResponseEntity<>(
                ApiResponse.ok("Workout item created", workoutService.createCatalogItem(request)),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update workout item")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutCatalogResponse>> updateWorkout(
            @PathVariable UUID id,
            @Valid @RequestBody WorkoutCatalogRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Workout item updated", workoutService.updateCatalogItem(id, request)));
    }

    @Operation(summary = "Delete workout item")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkout(@PathVariable UUID id) {
        workoutService.deleteCatalogItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Workout item deleted", null));
    }

    @Operation(summary = "Get workout item details")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutCatalogResponse>> getWorkout(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.getCatalogItem(id)));
    }
}
