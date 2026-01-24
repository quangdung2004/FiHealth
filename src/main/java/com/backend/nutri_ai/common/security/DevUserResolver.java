package com.backend.nutri_ai.common.security;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevUserResolver {

    private static final String DEV_EMAIL = "dev@local";

    private final AppUserRepository userRepository;

    public AppUser getCurrentUser() {
        return userRepository.findByEmail(DEV_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "Dev user not found. Did DevUserSeeder run?"
                ));
    }
}
