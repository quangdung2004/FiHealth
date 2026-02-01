package com.backend.nutri_ai.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNotificationRequest {

    private String title;
    private String content;
    private boolean active;
}
