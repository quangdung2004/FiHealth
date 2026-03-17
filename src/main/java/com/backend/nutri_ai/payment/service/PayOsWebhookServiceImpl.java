package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.auth.Mail.service.MailService;
import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.common.enums.UserEventType;
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
    private final MailService mailService;
    private final UserEventService eventService;

    @Override
    @Transactional
    public void handleWebhook(PayOsWebhookRequest request) {

        // 1) chỉ xử lý khi success
        if (request == null || !"00".equals(request.getCode())) {
            eventService.track(UserEventType.PAYMENT_WEBHOOK_IGNORED, null, true,
                    "SYSTEM", null, "{\"code\":\"" + (request != null ? request.getCode() : null) + "\"}", null);
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
            eventService.track(UserEventType.PAYMENT_WEBHOOK_TX_NOT_FOUND, null, false,
                    "SYSTEM", orderCode, null, null);
            return;
        }

        // 3) idempotent: đã PAID thì bỏ qua
        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            eventService.track(UserEventType.PAYMENT_WEBHOOK_ALREADY_PAID, tx.getUser()!=null?tx.getUser().getId():null, true,
                    "SYSTEM", orderCode, null, null);
            return;
        }

        Long webhookAmount = request.getData().getAmount();
        if (webhookAmount != null && tx.getAmount() != null && !tx.getAmount().equals(webhookAmount)) {
            eventService.track(UserEventType.PAYMENT_AMOUNT_MISMATCH, tx.getUser()!=null?tx.getUser().getId():null, false,
                    "SYSTEM", orderCode, "{\"dbAmount\":" + tx.getAmount() + ",\"webhookAmount\":" + webhookAmount + "}", null);
        }
        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPaidAt(Instant.now());

        // 5) upgrade membership
        if (tx.getUser() == null) {
            log.warn("Transaction has no user – orderCode={}", orderCode);
            return;
        }
        membershipService.upgrade(tx.getUser(), tx.getDurationDays());

        try {
            String email = tx.getUser().getEmail();
            String fullName = tx.getUser().getFullName();
            mailService.sendPremiumThankYouMail(
                    email,
                    fullName,
                    tx.getDurationDays() != null ? tx.getDurationDays() : 0,
                    tx.getAmount(),
                    orderCode
            );
        } catch (Exception ex) {
            log.warn("Send thank-you mail failed – orderCode={} – reason={}", orderCode, ex.getMessage());
        }

        eventService.track(
                UserEventType.PAYMENT_WEBHOOK_SUCCESS,
                tx.getUser().getId(),
                true,
                "SYSTEM",
                orderCode,
                "{\"amount\":" + webhookAmount + ",\"days\":" + tx.getDurationDays() + "}",
                null
        );
    }
}
