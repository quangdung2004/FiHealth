package com.backend.nutri_ai.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MetricsOverviewDto {
    private long newUsers;          // từ AppUser.createdAt
    private long verifiedUsers;     // event AUTH_EMAIL_VERIFIED
    private long premiumUpgrades;   // event MEMBERSHIP_UPGRADED
    private long loginCount;        // total login success
    private long dau;               // distinct user login success
}
