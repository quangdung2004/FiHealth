package com.backend.nutri_ai.payment.controller;

import com.backend.nutri_ai.payment.dto.request.PayOsWebhookRequest;
import com.backend.nutri_ai.payment.security.PayOsSignatureVerifier;
import com.backend.nutri_ai.payment.service.inf.PayOsWebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class PayOsWebhookController {

    private final PayOsSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;
    private final PayOsWebhookService webhookService;

    @PostMapping("/payos")
    public ResponseEntity<Void> webhook(@RequestBody String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            String signature = root.get("signature").asText();

            if (!signatureVerifier.verify(rawBody, signature)) {
                log.warn("PAYOS SIGNATURE INVALID – IGNORED");
                return ResponseEntity.ok().build();
            }

            PayOsWebhookRequest request =
                    objectMapper.readValue(rawBody, PayOsWebhookRequest.class);

            webhookService.handleWebhook(request);

        } catch (Exception e) {
            log.error("PAYOS WEBHOOK ERROR", e);
        }

        // PayOS chỉ cần HTTP 200
        return ResponseEntity.ok().build();
    }
}
