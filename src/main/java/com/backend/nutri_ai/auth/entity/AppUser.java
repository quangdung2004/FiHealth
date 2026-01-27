package com.backend.nutri_ai.auth.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="app_user", indexes = {
        @Index(name="ux_user_email", columnList="email", unique=true)
})
@Getter @Setter
    public class AppUser extends BaseEntity {
    @Column(nullable=false, length=120)
    private String email;
    @Column(nullable=false, length=120)
    private String FullName;
    @Column(nullable=false, length=200)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private UserStatus status = UserStatus.ACTIVE;

    private Instant lastLoginAt;

    @Column(length=300)
    private String blockedReason;
    @Column(nullable = false)
    private Integer tokenVersion = 0;
    @OneToOne(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private UserProfile profile;
}

