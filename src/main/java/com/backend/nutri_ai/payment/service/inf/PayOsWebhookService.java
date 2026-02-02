package com.backend.nutri_ai.payment.service.inf;

import com.backend.nutri_ai.payment.dto.request.PayOsWebhookRequest;

public interface PayOsWebhookService {
    void handleWebhook(PayOsWebhookRequest request);
}