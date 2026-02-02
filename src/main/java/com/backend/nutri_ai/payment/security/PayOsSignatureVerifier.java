package com.backend.nutri_ai.payment.security;

import com.backend.nutri_ai.payment.config.PayOsConfig;
import com.backend.nutri_ai.payment.util.PayOsSignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayOsSignatureVerifier {

    private final PayOsConfig payOsConfig;
    private final ObjectMapper objectMapper;

    public boolean verify(String rawBody, String payosSignature) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            JsonNode data = root.get("data");
            if (data == null || !data.isObject()) return false;

            // 1) sort key alphabet
            Map<String, String> params = new TreeMap<>();
            data.fields().forEachRemaining(e -> {
                String key = e.getKey();
                JsonNode v = e.getValue();

                // payOS: null/undefined => ""
                if (v == null || v.isNull()) {
                    params.put(key, "");
                    return;
                }

                // giữ nguyên value (KHÔNG encode)
                String value = v.isTextual() ? v.asText() : v.toString();
                if ("null".equalsIgnoreCase(value) || "undefined".equalsIgnoreCase(value)) {
                    value = "";
                }
                params.put(key, value);
            });

            // 2) build raw data: key=value&...
            String rawData = params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 3) HMAC SHA256
            String localSignature = PayOsSignatureUtil.hmacSha256(rawData, payOsConfig.getChecksumKey());

            log.info("FINAL_RAW_DATA   = {}", rawData);
            log.info("SIGNATURE_LOCAL  = {}", localSignature);
            log.info("SIGNATURE_PAYOS  = {}", payosSignature);

            return localSignature.equalsIgnoreCase(payosSignature);

        } catch (Exception e) {
            log.error("Verify PayOS signature failed", e);
            return false;
        }
    }
}