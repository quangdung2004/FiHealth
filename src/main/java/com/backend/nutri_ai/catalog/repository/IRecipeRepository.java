package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IRecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("SELECT r FROM Recipe r WHERE " +
            "(:q IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:active IS NULL OR r.active = :active)")
    Page<Recipe> searchRecipes(@Param("q") String query,
                               @Param("active") Boolean active,
                               Pageable pageable);
}