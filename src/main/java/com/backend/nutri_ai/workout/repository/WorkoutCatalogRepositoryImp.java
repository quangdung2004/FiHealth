package com.backend.nutri_ai.workout.repository;

import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.entity.WorkoutCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkoutCatalogRepositoryImp {

    private final IWorkoutCatalogRepository workoutRepository;

    public Page<WorkoutCatalog> searchWorkouts(String query, Boolean active, WorkoutLevel level, WorkoutType type,
            Pageable pageable) {
        return workoutRepository.searchWorkouts(query, active, level, type, pageable);
    }

    public WorkoutCatalog save(WorkoutCatalog workout) {
        return workoutRepository.save(workout);
    }

    public Optional<WorkoutCatalog> findById(UUID id) {
        return workoutRepository.findById(id);
    }

    public void deleteById(UUID id) {
        workoutRepository.deleteById(id);
    }

    public List<WorkoutCatalog> findAllActive() {
        return workoutRepository.findByActiveTrue();
    }
}
