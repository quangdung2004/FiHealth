package com.backend.nutri_ai.auth.repository;

import com.backend.nutri_ai.auth.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserProfileRepo extends JpaRepository<UserProfile, UUID> {
}