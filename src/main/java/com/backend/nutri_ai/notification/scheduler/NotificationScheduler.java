package com.backend.nutri_ai.notification.scheduler;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.MembershipType;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.notification.config.EmailProducer;
import com.backend.nutri_ai.notification.config.EmailQueueMessage;
import com.backend.nutri_ai.notification.entity.Notification;
import com.backend.nutri_ai.notification.repository.NotificationRepository;
import com.backend.nutri_ai.notification.repository.NotificationSendLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepo;
    private final NotificationSendLogRepository logRepo;
    private final AppUserRepository userRepo;
    private final EmailProducer producer;


    @Scheduled(fixedRate = 60_000)
    public void dispatch() {

        Instant now = Instant.now();

        List<Notification> notifications =
                notificationRepo.findByActiveTrueAndStartAtBefore(now);

        for (Notification n : notifications) {

            List<AppUser> users = switch (n.getTargetGroup()) {

                case FREE -> userRepo.findByMembershipAndStatus(
                        MembershipType.FREE,
                        UserStatus.ACTIVE
                );

                case PREMIUM -> userRepo.findByMembershipAndStatus(
                        MembershipType.PREMIUM,
                        UserStatus.ACTIVE
                );

                case ALL -> userRepo.findByStatus(UserStatus.ACTIVE);
            };

            for (AppUser u : users) {

                boolean alreadySent =
                        logRepo.existsByUserIdAndNotificationId(
                                u.getId(),
                                n.getId()
                        );

                if (alreadySent) continue;


                producer.send(new EmailQueueMessage(
                        u.getId(),
                        n.getId(),
                        u.getEmail(),
                        n.getTitle(),
                        n.getContent()
                ));
            }
        }
    }
}
