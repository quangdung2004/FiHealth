package com.backend.nutri_ai.notification.config;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailQueueMessage implements Serializable {

    private UUID userId;
    private Long notificationId;

    private String to;
    private String subject;
    private String html;
}
