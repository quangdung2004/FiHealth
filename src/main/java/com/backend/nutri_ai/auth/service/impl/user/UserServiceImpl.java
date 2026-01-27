package com.backend.nutri_ai.auth.service.impl.user;


import com.backend.nutri_ai.auth.dto.request.user.ChangePasswordRequest;
import com.backend.nutri_ai.auth.dto.request.user.UserProfileRequest;
import com.backend.nutri_ai.auth.dto.response.user.UserDetailResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.UserProfile;
import com.backend.nutri_ai.auth.mapper.UserProfileMapper;
import com.backend.nutri_ai.auth.repo.AppUserRepo;

import com.backend.nutri_ai.auth.repository.UserProfileRepo;
import com.backend.nutri_ai.auth.service.inf.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppUserRepo appUserRepo;
    private final UserProfileRepo userProfileRepo;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    // --- SECURITY HELPER ---
    private AppUser getAuthenticatedUser() {
        // 1. Lấy chuỗi từ Token (JwtService đã setSubject là ID)
        String userIdString = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Chuyển đổi từ String sang UUID
        UUID userId;
        try {
            userId = UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token không hợp lệ: Subject không phải là UUID");
        }
        return appUserRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User với ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getCurrentUser() {
        AppUser user = getAuthenticatedUser();
        // Gọi Mapper để chuyển đổi
        return userProfileMapper.toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public void createProfile(UserProfileRequest request) {
        AppUser user = getAuthenticatedUser();

        // Validate nghiệp vụ
        if (user.getProfile() != null) {
            throw new IllegalStateException("Hồ sơ đã tồn tại. Vui lòng sử dụng tính năng cập nhật.");
        }

        log.info("Creating profile for user: {}", user.getEmail());

        // Tạo entity rỗng
        UserProfile profile = new UserProfile();
        profile.setUser(user);

        // Dùng Mapper để đổ dữ liệu vào
        userProfileMapper.updateEntityFromRequest(request, profile);

        userProfileRepo.save(profile);
    }

    @Override
    @Transactional
    public void updateProfile(UserProfileRequest request) {
        AppUser user = getAuthenticatedUser();
        UserProfile profile = user.getProfile();

        // Validate nghiệp vụ
        if (profile == null) {
            throw new IllegalStateException("Hồ sơ chưa tồn tại. Vui lòng tạo mới trước.");
        }

        log.info("Updating profile for user: {}", user.getEmail());

        // Dùng Mapper để update dữ liệu
        userProfileMapper.updateEntityFromRequest(request, profile);

        userProfileRepo.save(profile);
    }
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 1. Lấy user hiện tại từ Token
        AppUser user = getAuthenticatedUser();

        // 2. Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác"); // Nên dùng Custom Exception (ErrorCode.WRONG_PASSWORD)
        }

        // 3. Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // 4. (Optional) Kiểm tra mật khẩu mới không được trùng mật khẩu cũ
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        // 5. Mã hóa mật khẩu mới và lưu
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        appUserRepo.save(user);

        log.info("User {} changed password successfully", user.getEmail());
    }
}