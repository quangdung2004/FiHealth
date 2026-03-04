package com.backend.nutri_ai.dev;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"dev", "default"})
public class DevUserSeeder implements CommandLineRunner {

    public static final String DEV_EMAIL = "dev@gmail.com";
    private static final String DEV_PASSWORD = "dev123"; // plain

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    @Override
    public void run(String... args) {

        AppUser dev = userRepository.findByEmail(DEV_EMAIL)
                .orElseGet(AppUser::new);   // có thì lấy, không có thì new

        dev.setEmail(DEV_EMAIL);
        dev.setFullName("Dev User");
        dev.setPasswordHash(passwordEncoder.encode(DEV_PASSWORD));
        dev.setRole(UserRole.ADMIN);        // hoặc ADMIN nếu muốn
        dev.setStatus(UserStatus.ACTIVE);
        dev.setBlockedReason(null);

        userRepository.save(dev);

        System.out.println("✅ Dev user ensured: " + DEV_EMAIL);
    }
}
