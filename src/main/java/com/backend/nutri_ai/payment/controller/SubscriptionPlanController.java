package com.backend.nutri_ai.payment.controller;

import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.payment.dto.request.CreateOrUpdatePlanRequest;
import com.backend.nutri_ai.payment.dto.response.SubscriptionPlanResponse;
import com.backend.nutri_ai.payment.service.inf.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được tạo
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> create(@RequestBody CreateOrUpdatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được sửa
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> update(@PathVariable Long id, @RequestBody CreateOrUpdatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được xóa
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    // KHÔNG THÊM @PreAuthorize -> Mọi người đều xem được
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }
}