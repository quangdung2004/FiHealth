package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.MembershipType;
import com.backend.nutri_ai.common.enums.PlanType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class MembershipService {

    @Transactional
    public void upgrade(AppUser user, Integer days) {

        int daysToAdd = (days != null) ? days : 30;

        Instant now = Instant.now();
        Instant base = (user.getPremiumExpiredAt() != null && user.getPremiumExpiredAt().isAfter(now))
                ? user.getPremiumExpiredAt() : now;

        user.setMembership(MembershipType.PREMIUM);
        user.setPremiumExpiredAt(base.plus(daysToAdd, ChronoUnit.DAYS));
    }
}

