package com.backend.nutri_ai.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "email_template")
@Getter
@Setter
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ví dụ: INACTIVE_1_DAY
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 5000)
    private String content;

    private boolean active = true;
}
