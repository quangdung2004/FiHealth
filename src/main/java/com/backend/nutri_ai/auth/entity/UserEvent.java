package com.backend.nutri_ai.auth.entity;


import com.backend.nutri_ai.common.enums.UserEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_event", indexes = {
        @Index(name = "ix_user_event_type_time", columnList = "eventType, occurredAt"),
        @Index(name = "ix_user_event_user_time", columnList = "userId, occurredAt"),
        @Index(name = "ix_user_event_time", columnList = "occurredAt")
})
@Getter @Setter
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", length = 36)
    private UUID userId;


    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private UserEventType eventType;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(length = 45)
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(length = 32)
    private String source; // WEB/MOBILE/API

    @Column(nullable = false)
    private boolean success = true;

    @Column(name = "ref_id", length = 64)
    private String refId;

    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;
    @PrePersist
    void prePersist() {
        if (occurredAt == null) occurredAt = Instant.now();
    }
}
