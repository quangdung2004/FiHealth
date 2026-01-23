package com.backend.nutri_ai.assessment.service;

import com.backend.nutri_ai.assessment.dto.CreateAssessmentRequest;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.repo.BodyMetricsSnapshotRepo;
import com.backend.nutri_ai.assessment.repo.NutritionAssessmentRepo;
import com.backend.nutri_ai.assessment.service.NutritionAssessmentService;
import com.backend.nutri_ai.auth.entity.AppUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NutritionAssessmentServiceImpl
        implements NutritionAssessmentService {

    private final NutritionAssessmentRepo assessmentRepo;
    private final BodyMetricsSnapshotRepo metricsRepo;

    @Override
    @Transactional
    public NutritionAssessment createFullAssessment(AppUser user, CreateAssessmentRequest request) {

        // 1) validate tối thiểu để tránh crash
        if (user == null) throw new IllegalArgumentException("user is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getHeightCm() == null || request.getHeightCm() <= 0) throw new IllegalArgumentException("heightCm must be > 0");
        if (request.getWeightKg() == null || request.getWeightKg() <= 0) throw new IllegalArgumentException("weightKg must be > 0");
        if (request.getAge() == null || request.getAge() <= 0) throw new IllegalArgumentException("age must be > 0");
        if (request.getSex() == null) throw new IllegalArgumentException("sex is required");
        if (request.getActivityLevel() == null) throw new IllegalArgumentException("activityLevel is required");
        if (request.getGoal() == null) throw new IllegalArgumentException("goal is required");

        double heightM = request.getHeightCm() / 100.0;

        // 2) BMI
        double bmi = request.getWeightKg() / (heightM * heightM);

        // 3) BMR (Mifflin-St Jeor)
        // Nam: 10W + 6.25H - 5A + 5
        // Nữ:  10W + 6.25H - 5A - 161
        double bmr = calcBmr(request.getSex(), request.getWeightKg(), request.getHeightCm(), request.getAge());

        // 4) TDEE = BMR * activityFactor
        double activityFactor = activityFactor(request.getActivityLevel());
        double tdee = bmr * activityFactor;

        // 5) calorieTarget (theo goal + targetKgPerWeek nếu có)
        double calorieTarget = calcCalorieTarget(tdee, request.getGoal(), request.getTargetKgPerWeek());

        // 6) Macro (g/ngày)
        // Protein: 1.6g/kg (an toàn cho đa số), Fat: 0.8g/kg, Carb: phần còn lại
        double proteinG = 1.6 * request.getWeightKg();
        double fatG = 0.8 * request.getWeightKg();

        // kcal từ protein/fat
        double kcalFromProtein = proteinG * 4;
        double kcalFromFat = fatG * 9;

        // còn lại cho carb (>=0)
        double remaining = calorieTarget - (kcalFromProtein + kcalFromFat);
        double carbG = Math.max(0, remaining / 4);

        // 7) tạo entity assessment
        NutritionAssessment assessment = new NutritionAssessment();
        assessment.setUser(user);

        assessment.setSex(request.getSex());
        assessment.setAge(request.getAge());
        assessment.setHeightCm(request.getHeightCm());
        assessment.setWeightKg(request.getWeightKg());
        assessment.setActivityLevel(request.getActivityLevel());
        assessment.setGoal(request.getGoal());

        assessment.setTargetKgPerWeek(request.getTargetKgPerWeek());
        assessment.setMealsPerDay(request.getMealsPerDay() != null ? request.getMealsPerDay() : 3);
        assessment.setBudgetPerDayVnd(request.getBudgetPerDayVnd());
        assessment.setNotes(request.getNotes());
        assessment.setAllergies(request.getAllergies());


        // 8) tạo metrics snapshot + map quan hệ 1-1
        BodyMetricsSnapshot metrics = new BodyMetricsSnapshot();
        metrics.setBmi(round2(bmi));
        metrics.setBmr(round0(bmr));
        metrics.setTdee(round0(tdee));
        metrics.setCalorieTarget(round0(calorieTarget));

        metrics.setProteinG(round0(proteinG));
        metrics.setFatG(round0(fatG));
        metrics.setCarbG(round0(carbG));

        // map 2 chiều (tùy entity bạn set field tên gì)
        metrics.setAssessment(assessment);
        assessment.setMetrics(metrics);

        // 9) save: do cascade ALL trên assessment->metrics thì chỉ cần save assessment
        // nhưng để chắc chắn, bạn có thể save assessment, JPA sẽ cascade metrics
        NutritionAssessment saved = assessmentRepo.save(assessment);
        System.out.println("BMI = " + bmi);
        System.out.println("BMR = " + bmr);
        System.out.println("TDEE = " + tdee);
        System.out.println("CalorieTarget = " + calorieTarget);
        System.out.println("Protein/Fat/Carb = "
                + proteinG + "/" + fatG + "/" + carbG);

        return saved;
    }

    @Override
    public List<NutritionAssessment> getMyAssessments(AppUser user) {
        if (user == null) throw new IllegalArgumentException("user is required");
        return assessmentRepo.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public NutritionAssessment getById(UUID id, AppUser user) {
        return assessmentRepo.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
    }

    private double calcBmr(Enum<?> sex, double weightKg, int heightCm, int age) {
        // Vì enum Sex của bạn có thể là MALE/FEMALE, mình xử lý theo name()
        String s = sex.name().toUpperCase();
        double base = 10 * weightKg + 6.25 * heightCm - 5 * age;
        if (s.contains("FEMALE") || s.contains("WOMAN") || s.contains("NU")) {
            return base - 161;
        }
        // mặc định coi là male
        return base + 5;
    }

    private double activityFactor(Enum<?> activityLevel) {
        String a = activityLevel.name().toUpperCase();
        // bạn map theo enum của bạn (VD: SEDENTARY/LIGHT/MODERATE/ACTIVE/VERY_ACTIVE)
        if (a.contains("SEDENTARY") || a.contains("LOW")) return 1.2;
        if (a.contains("LIGHT")) return 1.375;
        if (a.contains("MODERATE") || a.contains("MEDIUM")) return 1.55;
        if (a.contains("ACTIVE")) return 1.725;
        if (a.contains("VERY")) return 1.9;
        return 1.55; // default
    }

    private double calcCalorieTarget(double tdee, Enum<?> goal, Double targetKgPerWeek) {
        String g = goal.name().toUpperCase();

        // nếu có targetKgPerWeek: 1 kg ~ 7700 kcal => /7 ngày
        if (targetKgPerWeek != null && targetKgPerWeek != 0) {
            double dailyDelta = (targetKgPerWeek * 7700.0) / 7.0;
            // giảm cân => targetKgPerWeek âm? hay dương? tuỳ bạn nhập
            // Mình xử lý: nếu goal là LOSE thì trừ, GAIN thì cộng
            if (g.contains("LOSE") || g.contains("CUT") || g.contains("GIAM")) {
                return tdee - Math.abs(dailyDelta);
            }
            if (g.contains("GAIN") || g.contains("BULK") || g.contains("TANG")) {
                return tdee + Math.abs(dailyDelta);
            }
        }

        // không có targetKgPerWeek thì dùng preset nhẹ nhàng
        if (g.contains("LOSE") || g.contains("CUT") || g.contains("GIAM")) return tdee - 400;
        if (g.contains("GAIN") || g.contains("BULK") || g.contains("TANG")) return tdee + 300;

        // maintain
        return tdee;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private int round0(double v) {
        return (int) Math.round(v);
    }
}
