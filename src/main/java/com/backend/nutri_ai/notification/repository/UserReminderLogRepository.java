package com.backend.nutri_ai.notification.repository;
import com.backend.nutri_ai.notification.entity.UserReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserReminderLogRepository
        extends JpaRepository<UserReminderLog, Long> {

    Optional<UserReminderLog> findByUserId(UUID userId);
}
