package com.backend.nutri_ai.auth.service.impl.analytics;


import com.backend.nutri_ai.auth.dto.response.MetricsOverviewDto;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.auth.repository.UserEventRepository;
import com.backend.nutri_ai.common.enums.UserEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final UserEventRepository eventRepo;
    private final AppUserRepository userRepo;

    public MetricsOverviewDto overview(Instant from, Instant to) {
        long newUsers = userRepo.countByCreatedAtBetween(from, to);

        long verifiedUsers = eventRepo.countEventsByTypeAndRange(UserEventType.AUTH_REGISTER_VERIFY_SUCCESS, from, to);
        long premiumUpgrades = eventRepo.countEventsByTypeAndRange(UserEventType.MEMBERSHIP_UPGRADED, from, to);
        long loginCount = eventRepo.countEventsByTypeAndRange(UserEventType.AUTH_LOGIN_SUCCESS, from, to);
        long dau = eventRepo.countDistinctUserIdByTypeAndRange(UserEventType.AUTH_LOGIN_SUCCESS, from, to);

        return MetricsOverviewDto.builder()
                .newUsers(newUsers)
                .verifiedUsers(verifiedUsers)
                .premiumUpgrades(premiumUpgrades)
                .loginCount(loginCount)
                .dau(dau)
                .build();
    }
}
