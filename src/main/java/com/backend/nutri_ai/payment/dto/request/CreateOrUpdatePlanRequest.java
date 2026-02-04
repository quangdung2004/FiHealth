package com.backend.nutri_ai.payment.dto.request;

import com.backend.nutri_ai.common.enums.PlanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrUpdatePlanRequest {

    @NotNull
    private PlanType planType;

    @NotBlank
    private String name;

    @Min(0)
    private Long price;

    @Min(1)
    private Integer durationDays;

    private boolean active;
    private String description;
}
