package com.backend.nutri_ai.workout.repository;

import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.entity.WorkoutCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IWorkoutCatalogRepository extends JpaRepository<WorkoutCatalog, UUID> {

    @Query("SELECT w FROM WorkoutCatalog w WHERE " +
            "(:q IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:active IS NULL OR w.active = :active) " +
            "AND (:level IS NULL OR w.level = :level) " +
            "AND (:type IS NULL OR w.type = :type)")
    Page<WorkoutCatalog> searchWorkouts(@Param("q") String query,
            @Param("active") Boolean active,
            @Param("level") WorkoutLevel level,
            @Param("type") WorkoutType type,
            Pageable pageable);

    List<WorkoutCatalog> findByActiveTrue();
}
