package com.backend.nutri_ai.payment.util;

import com.backend.nutri_ai.payment.dto.request.PayOsCreatePaymentRequest;
import com.backend.nutri_ai.payment.dto.request.PayOsWebhookRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PayOsSignatureUtil {

    public static String createPaymentSignature(PayOsCreatePaymentRequest req, String key) {
        // Sắp xếp tham số theo alphabet (A-Z)
        Map<String, Object> params = new TreeMap<>();
        params.put("amount", req.getAmount());
        params.put("cancelUrl", req.getCancelUrl());
        params.put("description", req.getDescription());
        params.put("orderCode", req.getOrderCode());
        params.put("returnUrl", req.getReturnUrl());

        String raw = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return hmacSha256(raw, key);
    }

    public static String webhookSignature(PayOsWebhookRequest req, String key) {
        // Webhook của PayOS gửi dữ liệu lồng trong object 'data'
        // Bạn phải băm dữ liệu từ trong req.getData() thay vì lấy trực tiếp ở ngoài
        Map<String, Object> params = new TreeMap<>();
        params.put("amount", req.getData().getAmount());
        params.put("description", req.getData().getDescription());
        params.put("orderCode", req.getData().getOrderCode());
        params.put("status", req.getData().getStatus());

        String raw = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return hmacSha256(raw, key);
    }

    private static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes());
            return HexFormat.of().formatHex(raw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}