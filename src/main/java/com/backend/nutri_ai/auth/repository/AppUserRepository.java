package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.common.enums.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    /* ================= AUTH ================= */

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByIdAndStatusAndTokenVersion(
            UUID id,
            UserStatus status,
            Integer tokenVersion
    );

    Page<AppUser> findByRoleNot(UserRole role, Pageable pageable);

    /* ================= EMAIL / NOTIFICATION ================= */

    // 🔹 Gửi cho tất cả user còn ACTIVE
    List<AppUser> findByStatus(UserStatus status);

    // 🔹 Gửi theo membership (FREE / PREMIUM) + chỉ ACTIVE
    List<AppUser> findByMembershipAndStatus(
            MembershipType membership,
            UserStatus status
    );

    // 🔹 Gửi cho nhiều nhóm membership + chỉ ACTIVE
    List<AppUser> findByMembershipInAndStatus(
            List<MembershipType> memberships,
            UserStatus status
    );

    // 🔹 Đếm user ACTIVE theo nhóm (dashboard / preview)
    long countByMembershipAndStatus(
            MembershipType membership,
            UserStatus status
    );
}
