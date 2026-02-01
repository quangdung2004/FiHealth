package com.backend.nutri_ai.payment.dto.request;

import com.backend.nutri_ai.common.enums.PlanType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQrPaymentRequest {
    private PlanType planType;
}
