package com.backend.nutri_ai.auth.dto.response.user;

import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.MembershipType;
import com.backend.nutri_ai.common.enums.Sex;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDetailResponse {
    private UUID id;
    private String email;
    private String fullName;
    private boolean hasProfile; // Frontend dựa vào cờ này để redirect
    private MembershipType membership;
    private ProfileDetail profile;

    @Data
    @Builder
    public static class ProfileDetail {
        private Sex sex;
        private Integer age;
        private Integer heightCm;
        private Double currentWeightKg;
        private ActivityLevel activityLevel;

        private Goal goal;
        private Double targetWeightKg;
        private LocalDate goalDeadline;
        private String specificGoal;

        private List<String> allergies;
        private List<String> diseases;
        private String medicalNotes;
    }
}