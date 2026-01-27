package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.TemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TemplateItemRepository extends JpaRepository<TemplateItem, UUID> {
    List<TemplateItem> findByMealId(UUID mealId);
}
