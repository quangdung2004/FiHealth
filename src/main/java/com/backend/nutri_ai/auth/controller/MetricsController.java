package com.backend.nutri_ai.auth.controller;

import com.backend.nutri_ai.auth.dto.response.ApiResponse; // Import thêm cái này
import com.backend.nutri_ai.auth.dto.response.MetricsOverviewDto;
import com.backend.nutri_ai.auth.service.impl.analytics.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity; // Import thêm cái này
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/metrics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<MetricsOverviewDto>> overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        MetricsOverviewDto metrics = metricsService.overview(from, to);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
}