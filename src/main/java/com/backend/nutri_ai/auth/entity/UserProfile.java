package com.backend.nutri_ai.auth.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_profile")
@Getter @Setter
public class UserProfile extends BaseEntity {

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private AppUser user;

    // --- CHỈ SỐ CƠ BẢN ---
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

    // --- MỤC TIÊU (GOAL) ---
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Goal goal;

    @Column(name = "target_weight_kg")
    private Double targetWeightKg; // Cân nặng mong muốn

    @Column(name = "goal_deadline")
    private LocalDate goalDeadline; // Ngày muốn đạt được mục tiêu

    @Column(length=500)
    private String specificGoal; // Mô tả chi tiết (Optional)

    @Column(length=1000)
    private String medicalNotes; // Ghi chú y tế

    // --- DANH SÁCH (DỊ ỨNG & BỆNH) ---
    // Sử dụng @ElementCollection để tạo bảng phụ, tối ưu hơn so với lưu String JSON
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_allergies",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "allergy_name")
    private List<String> allergies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_diseases",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "disease_name")
    private List<String> diseases = new ArrayList<>();
}