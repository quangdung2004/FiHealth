package com.backend.nutri_ai.auth.dto.response;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import java.util.UUID;

@Getter
@Builder
public class BugReportAdminResponse {

    private UUID id;
    private String title;
    private String description;
    private String screen;

    private BugPriority priority;
    private BugStatus status;

    private UUID userId;
    private String userEmail;

    private Instant createdAt;
}
