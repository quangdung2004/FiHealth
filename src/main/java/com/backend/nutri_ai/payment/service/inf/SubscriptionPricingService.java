package com.backend.nutri_ai.payment.service.inf;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.PlanType;


public interface SubscriptionPricingService {

    Long calculatePrice(AppUser user, PlanType planType);
}
