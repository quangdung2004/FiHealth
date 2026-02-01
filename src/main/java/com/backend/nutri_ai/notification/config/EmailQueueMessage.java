package com.backend.nutri_ai.notification.config;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailQueueMessage {

    private UUID userId;
    private Long notificationId;

    private String to;
    private String subject;
    private String html;
}
