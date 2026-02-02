package com.backend.nutri_ai.workout.repository;

import com.backend.nutri_ai.workout.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IWorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {

    Optional<WorkoutPlan> findFirstByUserIdAndStatus(UUID userId, String status);

    List<WorkoutPlan> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, String status);

}
