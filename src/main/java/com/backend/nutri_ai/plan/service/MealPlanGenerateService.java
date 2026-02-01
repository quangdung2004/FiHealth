package com.backend.nutri_ai.plan.service;

import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.ai.service.MealPlanAiService;
import com.backend.nutri_ai.ai.service.MealPlanCandidateService;
import com.backend.nutri_ai.ai.service.MealPlanValidatorService;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.repository.NutritionAssessmentRepository;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.ForbiddenException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import com.backend.nutri_ai.plan.dto.MealPlanGenerateResponse;
import com.backend.nutri_ai.plan.entity.MealPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealPlanGenerateService {

    private final NutritionAssessmentRepository assessmentRepo;
    private final MealPlanCandidateService candidateService;
    private final MealPlanAiService aiService;
    private final MealPlanValidatorService validatorService;
    private final MealPlanPersistenceService persistenceService;

    public MealPlanGenerateResponse generate(AppUser user, UUID assessmentId, PlanPeriod period) {

        // ✅ Query luôn theo userId => tự chặn ownership
        NutritionAssessment assessment = assessmentRepo
                .findByIdWithMetricsAndUserId(assessmentId, user.getId())
                .orElseThrow(() -> new ForbiddenException("Không có quyền hoặc assessment không tồn tại"));

        BodyMetricsSnapshot metrics = assessment.getMetrics();
        if (metrics == null) {
            throw new ResourceNotFoundException("Assessment chưa có metrics snapshot");
        }

        var bundle = candidateService.buildCandidates(assessment, period);

        MealPlanAiOutput aiOut = aiService.generate(
                user,
                assessment,
                metrics,
                period,
                bundle.candidates()
        );

        Map<Integer, MealPlanValidatorService.DayTotals> totalsByDay =
                validatorService.validateAndComputeTotals(
                        assessment, metrics, period, bundle.candidates(), aiOut
                );

        MealPlan plan = persistenceService.persist(
                user,
                assessment,
                period,
                bundle.candidates(),
                aiOut,
                totalsByDay
        );

        return new MealPlanGenerateResponse(plan.getId(), plan.getTotalDays(), plan.getEstimatedTotalCostVnd());
    }
}
