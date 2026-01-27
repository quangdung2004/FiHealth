package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.TemplateMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TemplateMealRepository extends JpaRepository<TemplateMeal, UUID> {
    List<TemplateMeal> findByDayIdOrderByMealOrderAsc(UUID dayId);
}
