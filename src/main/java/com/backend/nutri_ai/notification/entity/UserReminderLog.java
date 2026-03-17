package com.backend.nutri_ai.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_reminder_log",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@Getter
@Setter
public class UserReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    // Lần gần nhất đã gửi reminder
    private Instant lastReminderAt;
}
