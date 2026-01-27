package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByIdAndStatusAndTokenVersion(
            UUID id,
            UserStatus status,
            Integer tokenVersion
    );
    Page<AppUser> findByRoleNot(UserRole role, Pageable pageable);
}

