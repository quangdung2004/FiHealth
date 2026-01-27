package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.MenuTemplate;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuTemplateRepository extends JpaRepository<MenuTemplate, UUID> {
    List<MenuTemplate> findByPeriod(PlanPeriod period);
}
