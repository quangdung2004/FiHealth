package com.backend.nutri_ai.ai.repository;

import com.backend.nutri_ai.ai.entity.AiInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiInteractionRepository extends JpaRepository<AiInteraction, UUID> {}
