package com.backend.nutri_ai.notification.service;

import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.common.enums.UserEventType;
import com.backend.nutri_ai.notification.dto.CreateNotificationRequest;
import com.backend.nutri_ai.notification.dto.UpdateNotificationRequest;
import com.backend.nutri_ai.notification.entity.Notification;
import com.backend.nutri_ai.notification.repository.NotificationRepository;
import com.backend.nutri_ai.notification.service.inf.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final UserEventService eventService;

    private final NotificationRepository repo;

    @Override
    public Notification create(CreateNotificationRequest r) {
        Notification n = new Notification();
        n.setTitle(r.getTitle());
        n.setContent(r.getContent());
        n.setType(r.getType());
        n.setTargetGroup(r.getTargetGroup());
        n.setStartAt(r.getStartAt());
        n.setRepeatIntervalMinutes(r.getRepeatIntervalMinutes());
        Notification saved = repo.save(n);

        eventService.track(
                UserEventType.NOTIFICATION_CREATED,
                null,
                true,
                "ADMIN",
                String.valueOf(saved.getId()),
                "{\"title\":\"" + saved.getTitle() + "\"}",
                null
        );

        return saved;

    }

    @Override
    public Notification update(Long id, UpdateNotificationRequest r) {
        Notification n = repo.findById(id).orElseThrow();
        n.setTitle(r.getTitle());
        n.setContent(r.getContent());
        n.setActive(r.isActive());
        Notification saved = repo.save(n);

        eventService.track(
                UserEventType.NOTIFICATION_UPDATED,
                null,
                true,
                "ADMIN",
                String.valueOf(saved.getId()),
                null,
                null
        );

        return saved;

    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);

        eventService.track(
                UserEventType.NOTIFICATION_DELETED,
                null,
                true,
                "ADMIN",
                String.valueOf(id),
                null,
                null
        );

    }

    @Override
    public List<Notification> getAll() {
        return repo.findAll();
    }
}
