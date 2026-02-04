package com.backend.nutri_ai.workout.repository;

import com.backend.nutri_ai.workout.entity.WorkoutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IWorkoutItemRepository extends JpaRepository<WorkoutItem, UUID> {
}
