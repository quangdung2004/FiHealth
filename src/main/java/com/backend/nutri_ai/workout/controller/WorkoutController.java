package com.backend.nutri_ai.workout.controller;

import com.backend.nutri_ai.workout.dto.response.WorkoutPlanResponse;
import com.backend.nutri_ai.workout.service.IWorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workout Plan", description = "Workout plan generation and retrieval")
public class WorkoutController {

    private final IWorkoutService workoutService;

    @Operation(summary = "Generate workout plan from assessment")
    @PostMapping("/recommend")
    public ResponseEntity<WorkoutPlanResponse> generatePlan(
            @RequestParam UUID assessmentId) {
        return new ResponseEntity<>(
                workoutService.generateWorkoutPlan(assessmentId),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get workout plan details")
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> getPlan(@PathVariable UUID id) {
        return ResponseEntity.ok(workoutService.getWorkoutPlan(id));
    }
}
