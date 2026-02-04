package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.common.enums.MembershipType;
import com.backend.nutri_ai.common.enums.PlanType;
import com.backend.nutri_ai.common.enums.UserEventType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class MembershipService {
    private UserEventService eventService;
    @Transactional
    public void upgrade(AppUser user, Integer days) {

        int daysToAdd = (days != null) ? days : 30;

        Instant now = Instant.now();
        Instant base = (user.getPremiumExpiredAt() != null && user.getPremiumExpiredAt().isAfter(now))
                ? user.getPremiumExpiredAt() : now;

        user.setMembership(MembershipType.PREMIUM);
        user.setPremiumExpiredAt(base.plus(daysToAdd, ChronoUnit.DAYS));
        eventService.track(
                UserEventType.MEMBERSHIP_UPGRADED,
                user.getId(),
                true,
                "SYSTEM",
                null,
                "{\"days\":" + daysToAdd + "}",
                null
        );
    }
}

