package com.backend.nutri_ai.workout.service;

import com.backend.nutri_ai.common.enums.Equipment;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.dto.request.WorkoutCatalogRequest;
import com.backend.nutri_ai.workout.dto.response.WorkoutCatalogResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutPlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IWorkoutService {

    // Catalog CRUD
    Page<WorkoutCatalogResponse> searchCatalog(String query, Boolean active, WorkoutLevel level, WorkoutType type,
            Pageable pageable);

    WorkoutCatalogResponse createCatalogItem(WorkoutCatalogRequest request);

    WorkoutCatalogResponse updateCatalogItem(UUID id, WorkoutCatalogRequest request);

    void deleteCatalogItem(UUID id);

    WorkoutCatalogResponse getCatalogItem(UUID id);

    // User APIs
    WorkoutPlanResponse getMyCurrentWorkoutPlan();

    List<WorkoutPlanResponse> getMyWorkoutHistory();

    // Plan Generation
    WorkoutPlanResponse generateWorkoutPlan(UUID assessmentId);

    WorkoutPlanResponse getWorkoutPlan(UUID id);

    // Completion tracking
    void toggleWorkoutItemCompletion(UUID itemId);
}
