package com.backend.nutri_ai.auth.service.inf;

import com.backend.nutri_ai.auth.dto.request.CreateBugReportRequest;
import com.backend.nutri_ai.auth.dto.response.BugReportAdminResponse;
import com.backend.nutri_ai.auth.entity.AppUser;

import com.backend.nutri_ai.auth.entity.BugReport;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BugReportService {

    void create(CreateBugReportRequest request);

    Page<BugReportAdminResponse> list(Pageable pageable);

    void updatePriority(UUID bugReportId, BugPriority priority);

    void updateStatus(UUID bugReportId, BugStatus status);
}

