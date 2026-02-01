package com.backend.nutri_ai.notification.repository;

import com.backend.nutri_ai.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByActiveTrueAndStartAtBefore(Instant now);
}
