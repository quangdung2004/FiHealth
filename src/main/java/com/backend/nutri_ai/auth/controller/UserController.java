package com.backend.nutri_ai.auth.controller;

import com.backend.nutri_ai.auth.dto.request.user.ChangePasswordRequest;
import com.backend.nutri_ai.auth.dto.request.user.UserProfileRequest;
import com.backend.nutri_ai.auth.dto.response.ApiResponse;
import com.backend.nutri_ai.auth.dto.response.user.UserDetailResponse;
import com.backend.nutri_ai.auth.service.inf.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. GET /me: Lấy thông tin user & check profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getMe() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getCurrentUser())
        );
    }

    // 2. POST /profile: Tạo mới (Onboarding)
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> createProfile(
            @Valid @RequestBody UserProfileRequest request
    ) {
        userService.createProfile(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody UserProfileRequest request
    ) {
        userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}