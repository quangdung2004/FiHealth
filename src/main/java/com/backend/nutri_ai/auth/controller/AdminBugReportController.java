package com.backend.nutri_ai.auth.controller;

import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.auth.dto.response.BugReportAdminResponse;
import com.backend.nutri_ai.auth.entity.BugReport;
import com.backend.nutri_ai.auth.service.inf.BugReportService;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/bug-reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBugReportController {

    private final BugReportService bugReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BugReportAdminResponse>>> list(
            Pageable pageable) {

        Page<BugReportAdminResponse> bugReports =
                bugReportService.list(pageable);

        return ResponseEntity.ok(ApiResponse.success(bugReports));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<ApiResponse<Void>> updatePriority(
            @PathVariable UUID id,
            @RequestParam BugPriority priority) {

        bugReportService.updatePriority(id, priority);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID id,
            @RequestParam BugStatus status) {

        bugReportService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
