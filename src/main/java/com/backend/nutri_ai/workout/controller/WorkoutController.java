package com.backend.nutri_ai.workout.controller;

import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.dto.response.WorkoutCatalogResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutPlanResponse;
import com.backend.nutri_ai.workout.service.IWorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workout Plan", description = "Workout plan generation and retrieval")
public class WorkoutController {

    private final IWorkoutService workoutService;

    @Operation(summary = "Search workout catalog")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<WorkoutCatalogResponse>>> searchCatalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) WorkoutLevel level,
            @RequestParam(required = false) WorkoutType type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.searchCatalog(q, active, level, type, pageable)));
    }

    @Operation(summary = "Generate workout plan from assessment")
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<WorkoutPlanResponse>> generatePlan(
            @RequestParam UUID assessmentId) {
        return new ResponseEntity<>(
                ApiResponse.ok("Workout plan generated", workoutService.generateWorkoutPlan(assessmentId)),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get my current active workout plan")
    @GetMapping("/my-current")
    public ResponseEntity<ApiResponse<WorkoutPlanResponse>> getMyCurrentPlan() {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.getMyCurrentWorkoutPlan()));
    }

    @Operation(summary = "Get my workout history")
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<WorkoutPlanResponse>>> getMyWorkoutHistory() {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.getMyWorkoutHistory()));
    }

    @Operation(summary = "Get workout plan details")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutPlanResponse>> getPlan(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workoutService.getWorkoutPlan(id)));
    }

    @Operation(summary = "Toggle workout item completion")
    @PutMapping("/items/{itemId}/toggle-complete")
    public ResponseEntity<ApiResponse<String>> toggleItemCompletion(@PathVariable UUID itemId) {
        workoutService.toggleWorkoutItemCompletion(itemId);
        return ResponseEntity.ok(ApiResponse.ok("Workout item completion toggled"));
    }
}
