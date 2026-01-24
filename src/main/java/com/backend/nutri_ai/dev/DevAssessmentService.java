package com.backend.nutri_ai.dev;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.repository.NutritionAssessmentRepository;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.Sex;
import com.backend.nutri_ai.common.security.DevUserResolver;
import com.backend.nutri_ai.dev.dto.DevAssessmentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DevAssessmentService {

    private final NutritionAssessmentRepository assessmentRepo;
    private final DevUserResolver devUserResolver;

    @Transactional
    public NutritionAssessment createForDevUser(DevAssessmentCreateRequest req) {
        AppUser user = devUserResolver.getCurrentUser();

        NutritionAssessment a = new NutritionAssessment();
        a.setUser(user);

        a.setSex(req != null && req.getSex() != null ? req.getSex() : Sex.MALE);
        a.setAge(req != null && req.getAge() != null ? req.getAge() : 22);
        a.setHeightCm(req != null && req.getHeightCm() != null ? req.getHeightCm() : 170);
        a.setWeightKg(req != null && req.getWeightKg() != null ? req.getWeightKg() : 65.0);

        a.setActivityLevel(req != null && req.getActivityLevel() != null
                ? req.getActivityLevel()
                : ActivityLevel.MODERATE);

        a.setGoal(req != null && req.getGoal() != null ? req.getGoal() : Goal.FAT_LOSS);

        a.setMealsPerDay(req != null && req.getMealsPerDay() != null ? req.getMealsPerDay() : 3);
        a.setBudgetPerDayVnd(req != null ? req.getBudgetPerDayVnd() : null);

        return assessmentRepo.save(a);
    }


    @Transactional(readOnly = true)
    public NutritionAssessment latestForDevUserOrNull() {
        AppUser user = devUserResolver.getCurrentUser();
        return assessmentRepo.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);
    }
}
