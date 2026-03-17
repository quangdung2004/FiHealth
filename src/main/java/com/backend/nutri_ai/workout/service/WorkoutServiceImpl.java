package com.backend.nutri_ai.workout.service;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.repository.NutritionAssessmentRepository;
import com.backend.nutri_ai.common.enums.ActivityLevel;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.dto.request.WorkoutCatalogRequest;
import com.backend.nutri_ai.workout.dto.response.WorkoutCatalogResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutPlanResponse;
import com.backend.nutri_ai.workout.entity.WorkoutCatalog;
import com.backend.nutri_ai.workout.entity.WorkoutDay;
import com.backend.nutri_ai.workout.entity.WorkoutItem;
import com.backend.nutri_ai.workout.entity.WorkoutPlan;
import com.backend.nutri_ai.workout.mapper.WorkoutMapper;
import com.backend.nutri_ai.workout.repository.IWorkoutItemRepository;
import com.backend.nutri_ai.workout.repository.IWorkoutPlanRepository;
import com.backend.nutri_ai.workout.repository.WorkoutCatalogRepositoryImp;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutServiceImpl implements IWorkoutService {

    private final WorkoutCatalogRepositoryImp catalogRepository;
    private final IWorkoutPlanRepository planRepository;
    private final IWorkoutItemRepository itemRepository;
    private final NutritionAssessmentRepository assessmentRepository;
    private final WorkoutMapper mapper;

    // MVP Constraints
    private static final int DEFAULT_TOTAL_WEEKS = 4;

    @Override
    public Page<WorkoutCatalogResponse> searchCatalog(String query, Boolean active, WorkoutLevel level,
            WorkoutType type, Pageable pageable) {
        return catalogRepository.searchWorkouts(query, active, level, type, pageable)
                .map(mapper::toCatalogResponse);
    }

    @Override
    @Transactional
    public WorkoutCatalogResponse createCatalogItem(WorkoutCatalogRequest request) {
        WorkoutCatalog entity = mapper.toCatalogEntity(request);
        WorkoutCatalog saved = catalogRepository.save(entity);
        log.info("Created workout catalog item: {}", saved.getId());
        return mapper.toCatalogResponse(saved);
    }

    @Override
    @Transactional
    public WorkoutCatalogResponse updateCatalogItem(UUID id, WorkoutCatalogRequest request) {
        WorkoutCatalog entity = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found detected with id: " + id));
        mapper.updateCatalogEntity(entity, request);
        WorkoutCatalog saved = catalogRepository.save(entity);
        log.info("Updated workout catalog item: {}", saved.getId());
        return mapper.toCatalogResponse(saved);
    }

    @Override
    public void deleteCatalogItem(UUID id) {
        catalogRepository.deleteById(id);
    }

    @Override
    public WorkoutCatalogResponse getCatalogItem(UUID id) {
        return catalogRepository.findById(id)
                .map(mapper::toCatalogResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found detected with id: " + id));
    }

    @Override
    @Transactional
    public WorkoutPlanResponse generateWorkoutPlan(UUID assessmentId) {
        NutritionAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Assessment not found detected with id: " + assessmentId));

        // 1. Determine Rules
        int daysPerWeek = calculateDaysPerWeek(assessment.getActivityLevel());
        Goal goal = assessment.getGoal();
        WorkoutLevel level = WorkoutLevel.Beginner; // Default for MVP

        // 2. Fetch all active exercises
        List<WorkoutCatalog> allExercises = catalogRepository.findAllActive();
        if (allExercises.isEmpty()) {
            throw new ResourceNotFoundException("Catalog is empty. Please seed catalog first.");
        }

        // 3. Create Plan
        WorkoutPlan plan = new WorkoutPlan();
        plan.setAssessment(assessment);
        plan.setUser(assessment.getUser());
        plan.setDaysPerWeek(daysPerWeek);
        plan.setTotalWeeks(DEFAULT_TOTAL_WEEKS);
        plan.setStatus("ACTIVE");

        // 4. Generate Days
        List<WorkoutDay> days = new ArrayList<>();
        for (int i = 1; i <= daysPerWeek; i++) {
            WorkoutDay day = createDay(plan, i, goal, level, allExercises, daysPerWeek);
            days.add(day);
        }
        plan.setDays(days);

        WorkoutPlan saved = planRepository.save(plan);
        log.info("Generated workout plan: {} for assessment: {}", saved.getId(), assessmentId);
        return mapper.toPlanResponse(saved);
    }

    @Override
    public WorkoutPlanResponse getWorkoutPlan(UUID id) {
        return planRepository.findById(id)
                .map(mapper::toPlanResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found detected with id: " + id));
    }

    @Override
    public WorkoutPlanResponse getMyCurrentWorkoutPlan() {
        UUID userId = getCurrentUserId();
        // Return active plan or throw generic resource not found which maps to 404
        return planRepository.findFirstByUserIdAndStatus(userId, "ACTIVE")
                .map(mapper::toPlanResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No active workout plan found for current user"));
    }

    @Override
    public List<WorkoutPlanResponse> getMyWorkoutHistory() {
        UUID userId = getCurrentUserId();

        return planRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, "COMPLETED")
                .stream()
                .map(mapper::toPlanResponse)
                .collect(Collectors.toList());
    }

    private UUID getCurrentUserId() {
        String userIdString = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            // This should not happen if JwtAuthenticationFilter validates correctly
            throw new RuntimeException("Invalid User ID in token or not authenticated");
        }
    }

    // ===== Helper Logic =====

    private int calculateDaysPerWeek(ActivityLevel activityLevel) {
        if (activityLevel == null)
            return 3;
        switch (activityLevel) {
            case SEDENTARY:
                return 3;
            case LIGHT:
            case MODERATE:
                return 4;
            case ACTIVE:
            case VERY_ACTIVE:
                return 5;
            default:
                return 3;
        }
    }

    private WorkoutDay createDay(WorkoutPlan plan, int dayIndex, Goal goal, WorkoutLevel level,
            List<WorkoutCatalog> allExercises, int daysPerWeek) {
        WorkoutDay day = new WorkoutDay();
        day.setWorkoutPlan(plan);
        day.setDayIndex(dayIndex);

        String focus = determineFocus(dayIndex, goal, daysPerWeek);
        day.setFocus(focus);
        day.setTotalDurationMin(45); // Approximate
        day.setCaloriesEstimate(300);

        List<WorkoutItem> items = selectExercisesForFocus(day, focus, goal, level, allExercises);
        day.setItems(items);

        return day;
    }

    private String determineFocus(int dayIndex, Goal goal, int daysPerWeek) {
        if (goal == Goal.FAT_LOSS) {
            // Full Body / Cardio mix
            if (daysPerWeek == 3) {
                if (dayIndex == 2)
                    return "CARDIO";
                return "FULL_BODY";
            }
            // More days: alternate
            if (dayIndex % 2 == 0)
                return "CARDIO";
            return "FULL_BODY";
        } else {
            // MUSCLE_GAIN (Upper/Lower Split)
            if (dayIndex % 2 != 0)
                return "UPPER"; // 1, 3, 5
            return "LOWER"; // 2, 4
        }
    }

    private List<WorkoutItem> selectExercisesForFocus(WorkoutDay day, String focus, Goal goal, WorkoutLevel level,
            List<WorkoutCatalog> allExercises) {
        List<WorkoutItem> items = new ArrayList<>();
        List<WorkoutCatalog> candidates = allExercises.stream()
                // Filter by level (simplified: include <= level)
                // For MVP just use all or match exact level if possible
                .filter(e -> e.getLevel() == level || e.getLevel() == WorkoutLevel.Beginner)
                .collect(Collectors.toList());

        // Simple selection logic
        if (focus.equals("CARDIO")) {
            addExercisesRequest(day, items, candidates, WorkoutType.CARDIO, 3);
            addExercisesRequest(day, items, candidates, WorkoutType.HIIT, 1);
        } else if (focus.equals("UPPER")) {
            addExercisesRequest(day, items, candidates, WorkoutType.STRENGTH, 5, "chest,back,shoulder,arm");
        } else if (focus.equals("LOWER")) {
            addExercisesRequest(day, items, candidates, WorkoutType.STRENGTH, 5, "leg,glute,calf");
        } else {
            // FULL BODY
            addExercisesRequest(day, items, candidates, WorkoutType.STRENGTH, 4);
            addExercisesRequest(day, items, candidates, WorkoutType.CARDIO, 1);
        }

        return items;
    }

    private void addExercisesRequest(WorkoutDay day, List<WorkoutItem> items, List<WorkoutCatalog> candidates,
            WorkoutType type, int count) {
        addExercisesRequest(day, items, candidates, type, count, null);
    }

    private void addExercisesRequest(WorkoutDay day, List<WorkoutItem> items, List<WorkoutCatalog> candidates,
            WorkoutType type, int count, String muscleFilter) {
        List<WorkoutCatalog> filtered = candidates.stream()
                .filter(e -> e.getType() == type)
                .filter(e -> {
                    if (muscleFilter == null)
                        return true;
                    if (e.getMuscleGroups() == null)
                        return false;
                    // Simple string check
                    for (String m : muscleFilter.split(",")) {
                        if (e.getMuscleGroups().toLowerCase().contains(m))
                            return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        Collections.shuffle(filtered);

        for (int i = 0; i < Math.min(count, filtered.size()); i++) {
            WorkoutCatalog ex = filtered.get(i);
            WorkoutItem item = new WorkoutItem();
            item.setWorkoutDay(day);
            item.setWorkoutCatalog(ex);
            item.setOrderIndex(items.size() + 1);

            // Set default sets/reps based on type
            if (type == WorkoutType.CARDIO || type == WorkoutType.HIIT) {
                item.setDurationSec(60);
                item.setRestSec(30);
                item.setSets(3);
            } else {
                item.setSets(3);
                item.setReps(10);
                item.setRestSec(60);
            }
            items.add(item);
        }
    }

    @Override
    @Transactional
    public void toggleWorkoutItemCompletion(UUID itemId) {
        // 1. Find WorkoutItem
        WorkoutItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout item not found with id: " + itemId));

        // 2. Verify user ownership through WorkoutPlan
        UUID currentUserId = getCurrentUserId();
        WorkoutPlan plan = item.getWorkoutDay().getWorkoutPlan();

        if (!plan.getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("You don't have permission to modify this workout item");
        }

        // 3. Toggle completed status
        item.setCompleted(!item.getCompleted());
        itemRepository.save(item);

        log.info("Toggled workout item {} completion to: {}", itemId, item.getCompleted());

        // 4. Check if all items in the plan are completed
        boolean allCompleted = plan.getDays().stream()
                .flatMap(day -> day.getItems().stream())
                .allMatch(WorkoutItem::getCompleted);

        // 5. If all completed, update plan status to COMPLETED
        if (allCompleted && "ACTIVE".equals(plan.getStatus())) {
            plan.setStatus("COMPLETED");
            planRepository.save(plan);
            log.info("Workout plan {} marked as COMPLETED", plan.getId());
        }
    }
}
