package com.backend.nutri_ai.notification.controller;

import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.notification.dto.CreateNotificationRequest;
import com.backend.nutri_ai.notification.dto.UpdateNotificationRequest;
import com.backend.nutri_ai.notification.service.inf.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ApiResponse<?> create(@RequestBody CreateNotificationRequest req) {
        return ApiResponse.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(
            @PathVariable Long id,
            @RequestBody UpdateNotificationRequest req
    ) {
        return ApiResponse.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("DELETED", null);
    }

    @GetMapping
    public ApiResponse<?> getAll() {
        return ApiResponse.ok(service.getAll());
    }
}
