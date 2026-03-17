package com.backend.nutri_ai.auth.controller;

import com.backend.nutri_ai.auth.dto.request.CreateBugReportRequest;
import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.service.inf.BugReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bug-reports")
@RequiredArgsConstructor
public class BugReportController {

    private final BugReportService bugReportService;


    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBug(
            @RequestBody CreateBugReportRequest request
    ) {
        bugReportService.create(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
