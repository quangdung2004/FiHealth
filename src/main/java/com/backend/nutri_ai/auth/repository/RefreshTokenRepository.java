package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndIsEnableTrue(String token);

    @Modifying
    @Query("update RefreshToken r set r.isEnable = false where r.user.id = :userId")
    void disableAllByUserId(UUID userId);
}