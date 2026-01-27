package com.backend.nutri_ai.auth.controller;


import com.backend.nutri_ai.auth.dto.request.user.BanUserRequest;
import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.auth.dto.response.user.UserSummaryResponse;
import com.backend.nutri_ai.auth.service.inf.user.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// Bảo vệ toàn bộ Controller này, chỉ Role ADMIN mới vào được
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // 1. Lấy danh sách User (Có phân trang)
    // URL: GET /api/admin/users?page=0&size=10
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(adminService.getAllUsers(page, size))
        );
    }

    // 2. Ban User
    // URL: POST /api/admin/users/{userId}/ban
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(
            @PathVariable UUID userId,
            @Valid @RequestBody BanUserRequest request
    ) {
        adminService.banUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 3. Unban User
    // URL: POST /api/admin/users/{userId}/unban
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(
            @PathVariable UUID userId
    ) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}