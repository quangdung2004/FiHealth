package com.backend.nutri_ai.notification.service.inf;

import com.backend.nutri_ai.notification.dto.CreateNotificationRequest;
import com.backend.nutri_ai.notification.dto.UpdateNotificationRequest;
import com.backend.nutri_ai.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification create(CreateNotificationRequest request);
    Notification update(Long id, UpdateNotificationRequest request);
    void delete(Long id);
    List<Notification> getAll();
}
