package com.backend.nutri_ai.assessment.repository;

import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BodyMetricsSnapshotRepo extends JpaRepository<BodyMetricsSnapshot, UUID> {
}
