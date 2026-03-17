package com.backend.nutri_ai.auth.schedule;


import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.common.enums.UserEventType;
import com.backend.nutri_ai.common.enums.UserRole;
import com.backend.nutri_ai.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannedUserAutoDeleteJob {

    private final AppUserRepository userRepo;
    private final UserEventService eventService;

    // chạy 03:00 mỗi ngày
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Bangkok")
    @Transactional
    public void softDeleteUsersBannedOver30Days() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);

        int total = 0;
        while (true) {
            List<AppUser> batch = userRepo.findTop500ByStatusAndBlockedAtBefore(UserStatus.BLOCKED, cutoff);
            if (batch.isEmpty()) break;

            for (AppUser u : batch) {
                // an toàn: không động vào ADMIN
                if (u.getRole() == UserRole.ADMIN) continue;

                u.setStatus(UserStatus.DELETED);
                u.setTokenVersion(u.getTokenVersion() + 1);

                // lưu reason (tạm dùng blockedReason nếu bạn chưa có deletedReason)
                u.setBlockedReason("AUTO_SOFT_DELETE_BLOCKED_OVER_30D");

                // optional: làm sạch dữ liệu nhạy cảm
                u.setPasswordHash("DELETED");
            }

            userRepo.saveAll(batch);

            for (AppUser u : batch) {
                eventService.track(
                        UserEventType.ADMIN_USER_DELETED,
                        u.getId(),
                        true,
                        "SYSTEM",
                        null,
                        "{\"reason\":\"blocked_over_30d\"}",
                        null
                );
            }

            total += batch.size();
        }

        log.info("Auto soft deleted {} users (BLOCKED before {})", total, cutoff);
    }
}
