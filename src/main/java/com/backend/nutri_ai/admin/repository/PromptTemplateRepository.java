package com.backend.nutri_ai.admin.repository;

import com.backend.nutri_ai.admin.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, UUID> {
    Optional<PromptTemplate> findFirstByNameAndActiveTrueAndIsDefaultTrue(String name);
}
