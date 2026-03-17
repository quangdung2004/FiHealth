package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.BugReport;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface BugReportRepository extends JpaRepository<BugReport, UUID> {

    Page<BugReport> findByStatus(BugStatus status, Pageable pageable);

    Page<BugReport> findByPriority(BugPriority priority, Pageable pageable);
}

