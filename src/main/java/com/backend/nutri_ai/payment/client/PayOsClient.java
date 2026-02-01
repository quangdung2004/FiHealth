package com.backend.nutri_ai.payment.client;

import com.backend.nutri_ai.payment.config.PayOsConfig;
import com.backend.nutri_ai.payment.dto.request.PayOsCreatePaymentRequest;
import com.backend.nutri_ai.payment.dto.response.PayOsCreatePaymentResponse;
import com.backend.nutri_ai.payment.util.PayOsSignatureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PayOsClient {

    private final PayOsConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public PayOsCreatePaymentResponse createPayment(PayOsCreatePaymentRequest req) {
        // 1. Tạo chữ ký bảo mật
        req.setSignature(
                PayOsSignatureUtil.createPaymentSignature(req, config.getChecksumKey())
        );

        // 2. Thêm đầy đủ Header PayOS yêu cầu (XỬ LÝ LỖI 401 PAYOS)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", config.getClientId()); // Lấy từ file cấu hình
        headers.set("x-api-key", config.getApiKey());     // Lấy từ file cấu hình

        HttpEntity<PayOsCreatePaymentRequest> entity = new HttpEntity<>(req, headers);

        // 3. Gọi API
        return restTemplate.postForObject(
                "https://api-merchant.payos.vn/v2/payment-requests",
                entity,
                PayOsCreatePaymentResponse.class
        );
    }
}