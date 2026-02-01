package com.backend.nutri_ai.notification.repository;

import com.backend.nutri_ai.notification.entity.NotificationSendLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationSendLogRepository
        extends JpaRepository<NotificationSendLog, UUID> {


    boolean existsByUserIdAndNotificationId(
            UUID userId,
            long notificationId
    );


}
