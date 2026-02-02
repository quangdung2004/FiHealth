package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IFoodRepository extends JpaRepository<FoodItem, UUID> {

        @Query("SELECT f FROM FoodItem f WHERE " +
                        "(:q IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                        "AND (:active IS NULL OR f.active = :active)")
        Page<FoodItem> searchFoods(@Param("q") String query,
                        @Param("active") Boolean active,
                        Pageable pageable);

        @Query("SELECT f FROM FoodItem f WHERE " +
                        "LOWER(f.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "OR LOWER(f.tags) LIKE LOWER(CONCAT('%', :q, '%'))")
        Page<FoodItem> findByNameOrTagsContaining(@Param("q") String query, Pageable pageable);

        boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}