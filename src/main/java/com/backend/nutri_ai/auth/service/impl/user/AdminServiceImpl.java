package com.backend.nutri_ai.auth.service.impl.user;




import com.backend.nutri_ai.auth.dto.request.user.BanUserRequest;
import com.backend.nutri_ai.auth.dto.response.user.UserSummaryResponse;
import com.backend.nutri_ai.auth.entity.AppUser;

import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.auth.service.inf.user.AdminService;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.common.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AppUserRepository appUserRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getAllUsers(int page, int size) {
        // 1. Tạo Pageable (Sắp xếp theo ngày tạo mới nhất)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 2. Query DB loại trừ ADMIN
        Page<AppUser> userPage = appUserRepo.findByRoleNot(UserRole.ADMIN, pageRequest);

        // 3. Map sang DTO
        return userPage.map(user -> UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .blockedReason(user.getBlockedReason())
                .createdAt(user.getCreatedAt()) // Giả sử BaseEntity có getCreatedAt
                .build());
    }

    @Override
    @Transactional
    public void banUser(UUID userId, BanUserRequest request) {
        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("You are not allowed to ban this user");
        }

        user.setStatus(UserStatus.BLOCKED);
        user.setBlockedReason(request.getReason());
        user.setTokenVersion(user.getTokenVersion() + 1);

        appUserRepo.save(user);
        log.info("Admin banned user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void unbanUser(UUID userId) {
        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        user.setBlockedReason(null);

        appUserRepo.save(user);
        log.info("Admin unbanned user: {}", user.getEmail());
    }
}