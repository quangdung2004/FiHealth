package com.backend.nutri_ai.plan.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateMealPlanFromTemplateRequest {
    private LocalDate startDate; // optional
}
