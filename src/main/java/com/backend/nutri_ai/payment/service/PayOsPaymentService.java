package com.backend.nutri_ai.payment.service;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.PaymentStatus;
import com.backend.nutri_ai.common.enums.PlanType;
import com.backend.nutri_ai.common.utils.SecurityUtils; // Import tiện ích mới
import com.backend.nutri_ai.payment.client.PayOsClient;
import com.backend.nutri_ai.payment.config.PayOsConfig;
import com.backend.nutri_ai.payment.dto.request.PayOsCreatePaymentRequest;
import com.backend.nutri_ai.payment.dto.response.PayOsCreatePaymentResponse;
import com.backend.nutri_ai.payment.dto.response.QrPaymentResponse;
import com.backend.nutri_ai.payment.entity.PaymentTransaction;
import com.backend.nutri_ai.payment.entity.SubscriptionPlan;
import com.backend.nutri_ai.payment.repository.PaymentTransactionRepository;
import com.backend.nutri_ai.payment.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayOsPaymentService {

    private final PayOsClient payOsClient;
    private final SubscriptionPlanRepository planRepo;
    private final PaymentTransactionRepository txRepo;
    private final PayOsConfig config;
    private final SecurityUtils securityUtils;

    @Transactional
    public QrPaymentResponse createQr(PlanType planType) {
        AppUser user = securityUtils.getCurrentUser();

        SubscriptionPlan plan = planRepo
                .findByPlanTypeAndActiveTrue(planType)
                .orElseThrow(() -> new RuntimeException("Gói dịch vụ không tồn tại"));


        long orderCode = System.currentTimeMillis();
        Instant expiredAt = Instant.now().plusSeconds(600);

        PayOsCreatePaymentRequest req = new PayOsCreatePaymentRequest();
        req.setOrderCode(orderCode); // Đảm bảo DTO PayOsCreatePaymentRequest đã đổi sang kiểu Long/long
        req.setAmount(plan.getPrice());
        req.setDescription("Thanh toan NutriAI"); // Tránh dùng dấu tiếng Việt có thể gây lỗi Signature
        req.setReturnUrl(config.getReturnUrl());
        req.setCancelUrl(config.getReturnUrl());
        req.setExpiredAt(expiredAt.getEpochSecond());

        PayOsCreatePaymentResponse res = payOsClient.createPayment(req);

        // PayOsPaymentService#createQr

        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderCode(String.valueOf(orderCode));
        tx.setAmount(plan.getPrice());
        tx.setPlanType(planType);
        tx.setDurationDays(plan.getDurationDays());   // ✅ thêm dòng này
        tx.setUser(user);
        tx.setStatus(PaymentStatus.PENDING);
        tx.setExpiredAt(expiredAt);                  // ✅ thêm nếu muốn
        txRepo.save(tx);


        return QrPaymentResponse.builder()
                .orderCode(String.valueOf(orderCode))
                .amount(plan.getPrice())
                .qrCode(res.getQrCode())
                .checkoutUrl(res.getCheckoutUrl())
                .expiredAt(expiredAt.getEpochSecond())
                .build();
    }
}