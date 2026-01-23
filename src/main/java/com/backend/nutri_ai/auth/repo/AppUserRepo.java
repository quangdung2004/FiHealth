package com.backend.nutri_ai.auth.repo;

import com.backend.nutri_ai.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepo extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
}
