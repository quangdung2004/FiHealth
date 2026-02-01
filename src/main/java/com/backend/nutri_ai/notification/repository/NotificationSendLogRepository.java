package com.backend.nutri_ai.notification.repository;

import com.backend.nutri_ai.notification.entity.NotificationSendLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationSendLogRepository
        extends JpaRepository<NotificationSendLog, Long> {

    boolean existsByUserIdAndNotificationId(
            UUID userId,
            long notificationId
    );
}
