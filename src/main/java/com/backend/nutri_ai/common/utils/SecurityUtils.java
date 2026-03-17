package com.backend.nutri_ai.common.utils;

import com.backend.nutri_ai.auth.entity.AppUser;


import com.backend.nutri_ai.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final AppUserRepository appUserRepo;

    public AppUser getCurrentUser() {
        // Lấy subject (UUID) từ JWT đã được filter xác thực
        String userIdString = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            UUID userId = UUID.fromString(userIdString);
            return appUserRepo.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Token: Subject is not a UUID");
        }
    }

    public UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}