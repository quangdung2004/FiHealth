package com.backend.nutri_ai.notification.service;

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
        return repo.save(n);
    }

    @Override
    public Notification update(Long id, UpdateNotificationRequest r) {
        Notification n = repo.findById(id).orElseThrow();
        n.setTitle(r.getTitle());
        n.setContent(r.getContent());
        n.setActive(r.isActive());
        return repo.save(n);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Notification> getAll() {
        return repo.findAll();
    }
}
