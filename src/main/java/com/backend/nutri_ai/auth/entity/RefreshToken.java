package com.backend.nutri_ai.auth.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_token",
        indexes = {
                @Index(name = "ux_refresh_token_token", columnList = "token", unique = true),
                @Index(name = "ix_refresh_token_user", columnList = "user_id")
        }
)
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String token;

    @Column(name = "expire_time", nullable = false)
    private Instant expireTime;

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

    @Column(name = "ip_locate", length = 100)
    private String ipLocate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            columnDefinition = "BINARY(16)"
    )
    private AppUser user;
}
