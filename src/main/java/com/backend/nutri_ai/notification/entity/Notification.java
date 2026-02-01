package com.backend.nutri_ai.notification.entity;

import com.backend.nutri_ai.common.enums.NotificationType;
import com.backend.nutri_ai.common.enums.TargetGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private TargetGroup targetGroup;

    // thời điểm bắt đầu gửi
    private Instant startAt;

    // chu kỳ lặp (phút) – chỉ dùng cho REPEAT
    private Integer repeatIntervalMinutes;

    private boolean active = true;

    private Instant createdAt = Instant.now();
}

