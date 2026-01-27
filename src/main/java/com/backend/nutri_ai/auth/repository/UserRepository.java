package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.UserRole;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);

}
