package com.backend.nutri_ai.auth.service.inf.user;

import com.backend.nutri_ai.auth.dto.request.user.BanUserRequest;
import com.backend.nutri_ai.auth.dto.response.user.UserSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {
    Page<UserSummaryResponse> getAllUsers(int page, int size);
    void banUser(UUID userId, BanUserRequest request);
    void unbanUser(UUID userId);
}