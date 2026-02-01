package com.backend.nutri_ai.notification.scheduler;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.UserStatus;
import com.backend.nutri_ai.notification.config.EmailProducer;
import com.backend.nutri_ai.notification.config.EmailQueueMessage;
import com.backend.nutri_ai.notification.entity.EmailTemplate;
import com.backend.nutri_ai.notification.entity.UserReminderLog;
import com.backend.nutri_ai.notification.repository.EmailTemplateRepository;
import com.backend.nutri_ai.notification.repository.UserReminderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactiveUserReminderScheduler {

    private final AppUserRepository userRepo;
    private final EmailTemplateRepository templateRepo;
    private final UserReminderLogRepository logRepo;
    private final EmailProducer producer;

    /**
     * Chạy mỗi 30 phút
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void remindInactiveUsers() {

        Instant now = Instant.now();

        // ❗ User không login ≥ 1 ngày
        Instant inactiveThreshold = now.minus(1, ChronoUnit.DAYS);

        // ❗ Cooldown 2 ngày sau mỗi lần gửi
        Instant cooldownThreshold = now.minus(2, ChronoUnit.DAYS);

        List<AppUser> users = userRepo.findByStatus(UserStatus.ACTIVE);

        for (AppUser u : users) {

            // ✅ User mới login → bỏ
            if (u.getLastLoginAt() != null &&
                    u.getLastLoginAt().isAfter(inactiveThreshold)) {
                continue;
            }

            // 🔍 Kiểm tra cooldown
            UserReminderLog log = logRepo.findByUserId(u.getId()).orElse(null);

            if (log != null &&
                    log.getLastReminderAt().isAfter(cooldownThreshold)) {
                continue;
            }
            EmailTemplate tpl = templateRepo
                    .randomByCode("INACTIVE_1_DAY")
                    .orElse(null);

            if (tpl == null) {
                continue;
            }

            String content = tpl.getContent()
                    .replace("{{name}}", u.getFullName());


            producer.send(new EmailQueueMessage(
                    u.getId(),
                    null,
                    u.getEmail(),
                    tpl.getSubject(),
                    content
            ));

            if (log == null) {
                log = new UserReminderLog();
                log.setUserId(u.getId());
            }
            log.setLastReminderAt(now);
            logRepo.save(log);
        }
    }
}
