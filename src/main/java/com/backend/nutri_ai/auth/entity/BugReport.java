package com.backend.nutri_ai.auth.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bug_report", indexes = {
        @Index(name = "idx_bug_status", columnList = "status"),
        @Index(name = "idx_bug_priority", columnList = "priority")
})
@Getter @Setter
public class BugReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String screen; // ví dụ: PAYMENT, LOGIN, PROFILE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BugPriority priority = BugPriority.LOW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BugStatus status = BugStatus.OPEN;

    @Column(length = 500)
    private String adminNote; // ghi chú nội bộ
}