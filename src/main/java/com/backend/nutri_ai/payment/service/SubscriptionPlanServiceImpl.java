package com.backend.nutri_ai.payment.service;


import com.backend.nutri_ai.payment.dto.request.CreateOrUpdatePlanRequest;
import com.backend.nutri_ai.payment.dto.response.SubscriptionPlanResponse;
import com.backend.nutri_ai.payment.entity.SubscriptionPlan;
import com.backend.nutri_ai.payment.repository.SubscriptionPlanRepository;
import com.backend.nutri_ai.payment.service.inf.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository repo;

    @Override
    public SubscriptionPlanResponse create(CreateOrUpdatePlanRequest request) {
        if (repo.existsByPlanType(request.getPlanType())) {
            throw new RuntimeException("PLAN_TYPE_ALREADY_EXISTS");
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planType(request.getPlanType())
                .name(request.getName())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .active(request.isActive())
                .build();

        repo.save(plan);
        return toResponse(plan);
    }

    @Override
    public SubscriptionPlanResponse update(Long id, CreateOrUpdatePlanRequest request) {
        SubscriptionPlan plan = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("PLAN_NOT_FOUND"));

        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());
        plan.setActive(request.isActive());

        return toResponse(plan);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<SubscriptionPlanResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SubscriptionPlanResponse getActivePlanByType(Enum<?> planType) {
        SubscriptionPlan plan = repo
                .findByPlanTypeAndActiveTrue((com.backend.nutri_ai.common.enums.PlanType) planType)
                .orElseThrow(() -> new RuntimeException("PLAN_NOT_ACTIVE"));

        return toResponse(plan);
    }

    private SubscriptionPlanResponse toResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .id(plan.getId())
                .planType(plan.getPlanType())
                .name(plan.getName())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .active(plan.isActive())
                .build();
    }
}
