package com.backend.nutri_ai.auth.mapper;


import com.backend.nutri_ai.auth.dto.response.BugReportAdminResponse;
import com.backend.nutri_ai.auth.entity.BugReport;

public class BugReportMapper {

    public static BugReportAdminResponse toAdminResponse(BugReport bug) {
        return BugReportAdminResponse.builder()
                .id(bug.getId())
                .title(bug.getTitle())
                .description(bug.getDescription())
                .screen(bug.getScreen())
                .priority(bug.getPriority())
                .status(bug.getStatus())
                .userId(bug.getUser().getId())
                .userEmail(bug.getUser().getEmail())
                .createdAt(bug.getCreatedAt())
                .build();
    }
}
