package com.backend.nutri_ai.dev;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"dev", "default"})
public class DevUserSeeder implements CommandLineRunner {

    public static final String DEV_EMAIL = "dev@local";
    private static final String DEV_PASSWORD_HASH = "{noop}dev";
    private final AppUserRepository userRepository;

    @Override
    public void run(String... args) {
        userRepository.findByEmail(DEV_EMAIL).ifPresentOrElse(
                user -> {
                    // đã tồn tại → không làm gì
                },
                () -> {
                    AppUser dev = new AppUser();
                    dev.setEmail(DEV_EMAIL);
                    dev.setPasswordHash(DEV_PASSWORD_HASH);
                    dev.setRole(UserRole.USER); // hoặc USER
                    dev.setStatus(UserStatus.ACTIVE);
                    dev.setFullName("Dev User");
                    userRepository.save(dev);

                    System.out.println("✅ Dev user created: " + DEV_EMAIL);
                }
        );
    }
}

