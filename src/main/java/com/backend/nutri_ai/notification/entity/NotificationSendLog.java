package com.backend.nutri_ai.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notification_send_log",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"notification_id", "user_id"}
        )
)
@Getter
@Setter
public class NotificationSendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;
    private UUID userId;

    private Instant sentAt;
}

