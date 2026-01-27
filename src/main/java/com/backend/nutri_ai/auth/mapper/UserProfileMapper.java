package com.backend.nutri_ai.auth.mapper;



import com.backend.nutri_ai.auth.dto.request.user.UserProfileRequest;
import com.backend.nutri_ai.auth.dto.response.user.UserDetailResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.UserProfile;
import com.backend.nutri_ai.common.enums.Goal;

import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    /**
     * Chuyển đổi từ Entity (AppUser + UserProfile) sang Response DTO
     */
    public UserDetailResponse toUserDetailResponse(AppUser user) {
        UserProfile profile = user.getProfile();
        boolean hasProfile = (profile != null);

        // 1. Build phần thông tin User cơ bản
        UserDetailResponse.UserDetailResponseBuilder responseBuilder = UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .hasProfile(hasProfile);

        // 2. Nếu có Profile, map các trường chi tiết
        if (hasProfile) {
            responseBuilder.profile(UserDetailResponse.ProfileDetail.builder()
                    .sex(profile.getSex())
                    .age(profile.getAge())
                    .heightCm(profile.getHeightCm())
                    .currentWeightKg(profile.getCurrentWeightKg())
                    .activityLevel(profile.getActivityLevel())
                    .goal(profile.getGoal())
                    .targetWeightKg(profile.getTargetWeightKg())
                    .goalDeadline(profile.getGoalDeadline())
                    .specificGoal(profile.getSpecificGoal())
                    .medicalNotes(profile.getMedicalNotes())
                    // JPA trả về List nên gán trực tiếp được
                    .allergies(profile.getAllergies())
                    .diseases(profile.getDiseases())
                    .build());
        }

        return responseBuilder.build();
    }

    /**
     * Map dữ liệu từ Request vào Entity (Dùng cho cả Create và Update)
     * Thay vì tạo mới Entity, ta truyền Entity cũ vào để update các trường.
     */
    public void updateEntityFromRequest(UserProfileRequest request, UserProfile profile) {
        // 1. Chỉ số cơ bản
        profile.setSex(request.getSex());
        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setCurrentWeightKg(request.getCurrentWeightKg());
        profile.setActivityLevel(request.getActivityLevel());

        // 2. Mục tiêu (Xử lý logic MAINTENANCE tại đây hoặc Service đều được,
        // nhưng để Mapper xử lý việc gán dữ liệu là hợp lý nhất)
        profile.setGoal(request.getGoal());

        if (request.getGoal() == Goal.MAINTENANCE) {
            profile.setTargetWeightKg(request.getCurrentWeightKg());
            profile.setGoalDeadline(null);
        } else {
            profile.setTargetWeightKg(request.getTargetWeightKg());
            profile.setGoalDeadline(request.getGoalDeadline());
        }

        profile.setSpecificGoal(request.getSpecificGoal());
        profile.setMedicalNotes(request.getMedicalNotes());

        // 3. Xử lý List (Quan trọng: Xóa cũ, thêm mới để tránh trùng lặp/tham chiếu sai)
        if (request.getAllergies() != null) {
            profile.getAllergies().clear();
            profile.getAllergies().addAll(request.getAllergies());
        }

        if (request.getDiseases() != null) {
            profile.getDiseases().clear();
            profile.getDiseases().addAll(request.getDiseases());
        }
    }
}