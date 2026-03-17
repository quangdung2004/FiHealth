package com.backend.nutri_ai.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOsWebhookRequest {

    private String code;
    private String desc;
    private WebhookData data;
    private String signature;

    @Getter
    @Setter
    public static class WebhookData {

        private long orderCode;
        private long amount;
        private String description;
        private String reference;
        private String transactionDateTime;
        private String currency;
        private String paymentLinkId;

        // Optional – PAYOS KHÔNG KÝ
        private String accountNumber;
        private String counterAccountNumber;
        private String counterAccountBankId;
    }
}
