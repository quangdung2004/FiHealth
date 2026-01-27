package com.backend.nutri_ai.auth.service.inf.user;

import com.backend.nutri_ai.auth.dto.request.user.ChangePasswordRequest;
import com.backend.nutri_ai.auth.dto.request.user.UserProfileRequest;
import com.backend.nutri_ai.auth.dto.response.user.UserDetailResponse;
import com.backend.nutri_ai.auth.entity.AppUser;

public interface UserService {
    UserDetailResponse getCurrentUser();
    void createProfile(UserProfileRequest request);
    void updateProfile(UserProfileRequest request);
    void changePassword(ChangePasswordRequest request);
}
