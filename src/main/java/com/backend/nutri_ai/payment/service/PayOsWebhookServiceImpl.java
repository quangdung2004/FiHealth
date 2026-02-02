package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.payment.dto.request.PayOsWebhookRequest;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import com.backend.nutri_ai.payment.repository.PaymentTransactionRepository;
import com.backend.nutri_ai.payment.service.inf.PayOsWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayOsWebhookServiceImpl implements PayOsWebhookService {

    private final PaymentTransactionRepository txRepo;
    private final MembershipService membershipService;

    @Override
    @Transactional
    public void handleWebhook(PayOsWebhookRequest request) {

        // 1) chỉ xử lý khi success
        if (request == null || !"00".equals(request.getCode())) {
            log.info("Webhook ignored – code={}", request != null ? request.getCode() : null);
            return;
        }

        if (request.getData() == null || request.getData().getOrderCode() == 0) {
            log.info("Invalid webhook data");
            return;
        }

        String orderCode = String.valueOf(request.getData().getOrderCode());

        // 2) lock transaction để xử lý idempotent + tránh race
        PaymentTransaction tx = txRepo.findByOrderCodeForUpdate(orderCode).orElse(null);
        if (tx == null) {
            log.warn("Transaction not found – orderCode={}", orderCode);
            return;
        }

        // 3) idempotent: đã PAID thì bỏ qua
        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Already PAID – orderCode={}", orderCode);
            return;
        }

        Long webhookAmount = request.getData().getAmount();
        if (webhookAmount != null && tx.getAmount() != null && !tx.getAmount().equals(webhookAmount)) {
            log.warn("Amount mismatch – orderCode={}, dbAmount={}, webhookAmount={}",
                    orderCode, tx.getAmount(), webhookAmount);
            // tuỳ nghiệp vụ: return; hoặc vẫn xử lý
            // return;
        }

        // 4) update transaction
        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPaidAt(Instant.now());

        // 5) upgrade membership
        if (tx.getUser() == null) {
            log.warn("Transaction has no user – orderCode={}", orderCode);
            return;
        }
        membershipService.upgrade(tx.getUser(), tx.getDurationDays());

        log.info("PAYMENT SUCCESS | orderCode={} | amount={} | ref={}",
                orderCode,
                webhookAmount,
                request.getData().getReference()
        );
    }
}
