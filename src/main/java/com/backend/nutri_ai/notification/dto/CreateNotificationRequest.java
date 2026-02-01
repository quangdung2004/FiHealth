package com.backend.nutri_ai.notification.dto;

import com.backend.nutri_ai.common.enums.NotificationType;
import com.backend.nutri_ai.common.enums.TargetGroup;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateNotificationRequest {

    private String title;
    private String content;

    private NotificationType type;
    private TargetGroup targetGroup;

    private Instant startAt;
    private Integer repeatIntervalMinutes; // null nếu ONCE
}
