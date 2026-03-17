    package com.backend.nutri_ai.payment.dto.request;

    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    public class PayOsCreatePaymentRequest {
        private long orderCode; // Phải là long để PayOS chấp nhận
        private long amount;
        private String description;
        private String cancelUrl;
        private String returnUrl;
        private String signature;
        private long expiredAt;
    }