package com.backend.nutri_ai.payment.constant;

import java.time.Duration;

public class PaymentConstant {

    public static final Duration EXPIRED_TIME = Duration.ofMinutes(10);
    public static final String ORDER_PREFIX = "NUTRI-";

    private PaymentConstant() {}
}
