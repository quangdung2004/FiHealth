package com.backend.nutri_ai.ai.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.AiTask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="ai_interaction", indexes = {
        @Index(name="idx_ai_task", columnList="task"),
        @Index(name="idx_ai_assessment", columnList="assessment_id")
})
@Getter
@Setter
public class AiInteraction extends BaseEntity {

    @Column(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private UUID userId;

    @Column(name="assessment_id", columnDefinition="BINARY(16)")
    private UUID assessmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=40)
    private AiTask task;

    @Lob
    private String prompt;

    @Lob
    private String response;

    private Integer latencyMs;

    @Column(length=100)
    private String model;
}
