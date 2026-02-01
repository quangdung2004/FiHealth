package com.backend.nutri_ai.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QrPaymentResponse {

    private String orderCode;
    private Long amount;
    private String qrCode;
    private String checkoutUrl;
    private Long expiredAt;
}
