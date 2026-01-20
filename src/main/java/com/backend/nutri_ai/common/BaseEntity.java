package com.backend.nutri_ai.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @Column(name="updated_at", nullable=false)
    private Instant updatedAt;

    @PrePersist
    protected void prePersist() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = Instant.now();
    }
}
