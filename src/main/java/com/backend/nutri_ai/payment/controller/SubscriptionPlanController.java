package com.backend.nutri_ai.payment.controller;


import com.backend.nutri_ai.payment.dto.request.CreateOrUpdatePlanRequest;
import com.backend.nutri_ai.payment.dto.response.SubscriptionPlanResponse;
import com.backend.nutri_ai.payment.entity.SubscriptionPlan;
import com.backend.nutri_ai.payment.service.inf.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    @PostMapping
    public SubscriptionPlanResponse create(
            @RequestBody CreateOrUpdatePlanRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SubscriptionPlanResponse update(
            @PathVariable Long id,
            @RequestBody CreateOrUpdatePlanRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    public List<SubscriptionPlanResponse> getAll() {
        return service.getAll();
    }
}
