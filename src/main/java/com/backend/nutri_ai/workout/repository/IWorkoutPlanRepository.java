package com.backend.nutri_ai.workout.repository;

import com.backend.nutri_ai.workout.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IWorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
