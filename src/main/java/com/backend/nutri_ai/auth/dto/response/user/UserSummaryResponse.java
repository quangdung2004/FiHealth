package com.backend.nutri_ai.auth.dto.response.user;



import com.backend.nutri_ai.common.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserSummaryResponse {
    private UUID id;
    private String email;
    private String fullName;
    private UserStatus status;
    private Instant lastLoginAt;
    private String blockedReason;
    private Instant createdAt;
}