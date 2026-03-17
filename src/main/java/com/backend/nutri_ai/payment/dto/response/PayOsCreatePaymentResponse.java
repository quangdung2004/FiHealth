package com.backend.nutri_ai.payment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOsCreatePaymentResponse {

    private String code;
    private String desc;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String checkoutUrl;
        private String qrCode;
        private String orderCode;
    }

    // tiện dùng
    public String getCheckoutUrl() {
        return data != null ? data.getCheckoutUrl() : null;
    }

    public String getQrCode() {
        return data != null ? data.getQrCode() : null;
    }
}

