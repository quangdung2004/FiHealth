package com.backend.nutri_ai.auth.dto.request.user;

import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileRequest {
    @NotNull(message = "Giới tính là bắt buộc")
    private Sex sex;

    @NotNull(message = "Tuổi là bắt buộc")
    @Min(value = 1, message = "Tuổi không hợp lệ")
    @Max(value = 120, message = "Tuổi không hợp lệ")
    private Integer age;

    @NotNull(message = "Chiều cao là bắt buộc")
    @Min(value = 50, message = "Chiều cao không hợp lệ")
    private Integer heightCm;

    @NotNull(message = "Cân nặng hiện tại là bắt buộc")
    @Min(value = 2, message = "Cân nặng không hợp lệ")
    private Double currentWeightKg;

    @NotNull(message = "Mức độ vận động là bắt buộc")
    private ActivityLevel activityLevel;

    // --- GOAL ---
    @NotNull(message = "Mục tiêu là bắt buộc")
    private Goal goal;

    @Min(value = 2, message = "Cân nặng mục tiêu không hợp lệ")
    private Double targetWeightKg;

    @Future(message = "Ngày hoàn thành mục tiêu phải ở trong tương lai")
    private LocalDate goalDeadline;

    private String specificGoal;

    // --- LISTS ---
    private List<String> allergies;
    private List<String> diseases;
    private String medicalNotes;
}