package com.backend.nutri_ai.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PayOsWebhookRequest {
    private String code;
    private String desc;
    private WebhookData data; // Bắt buộc phải có lớp lồng này
    private String signature;

    @Getter @Setter
    public static class WebhookData {
        private long orderCode;
        private long amount;
        private String description;
        private String status; // Giá trị "PAID" nằm ở đây
    }

    // Thêm 2 hàm này để Controller cũ không bị lỗi compile
    public String getOrderCode() {
        return data != null ? String.valueOf(data.getOrderCode()) : "";
    }
    public String getStatus() {
        return data != null ? data.getStatus() : "";
    }
}