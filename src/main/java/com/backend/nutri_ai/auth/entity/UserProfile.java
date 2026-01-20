package com.backend.nutri_ai.auth.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Sex;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user_profile")
@Getter @Setter
public class UserProfile extends BaseEntity {

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Sex sex;

    @Column(nullable=false)
    private Integer age;

    @Column(nullable=false)
    private Integer heightCm;

    @Column(nullable=false)
    private Double currentWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ActivityLevel activityLevel;

    @Column(length=500)
    private String medicalNotes;

    @Column(length=500)
    private String allergies;
}

