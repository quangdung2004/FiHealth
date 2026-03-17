package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.TemplateDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TemplateDayRepository extends JpaRepository<TemplateDay, UUID> {
    List<TemplateDay> findByTemplateIdOrderByDayIndexAsc(UUID templateId);
}
