package com.backend.nutri_ai.payment.service.inf;

import com.backend.nutri_ai.payment.dto.request.CreateOrUpdatePlanRequest;
import com.backend.nutri_ai.payment.dto.response.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse create(CreateOrUpdatePlanRequest request);

    SubscriptionPlanResponse update(Long id, CreateOrUpdatePlanRequest request);

    void delete(Long id);

    List<SubscriptionPlanResponse> getAll();

    /**
     * Dùng cho payment
     * - chỉ lấy plan active
     * - throw exception nếu không tồn tại
     */
    SubscriptionPlanResponse getActivePlanByType(Enum<?> planType);
}
