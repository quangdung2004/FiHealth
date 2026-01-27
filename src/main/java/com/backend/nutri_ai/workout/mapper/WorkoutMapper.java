package com.backend.nutri_ai.workout.mapper;

import com.backend.nutri_ai.common.enums.Equipment;
import com.backend.nutri_ai.common.enums.WorkoutLevel;
import com.backend.nutri_ai.common.enums.WorkoutType;
import com.backend.nutri_ai.workout.dto.request.WorkoutCatalogRequest;
import com.backend.nutri_ai.workout.dto.response.WorkoutCatalogResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutDayResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutItemResponse;
import com.backend.nutri_ai.workout.dto.response.WorkoutPlanResponse;
import com.backend.nutri_ai.workout.entity.WorkoutCatalog;
import com.backend.nutri_ai.workout.entity.WorkoutDay;
import com.backend.nutri_ai.workout.entity.WorkoutItem;
import com.backend.nutri_ai.workout.entity.WorkoutPlan;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class WorkoutMapper {

    // ===== Catalog: Entity -> Response =====
    public WorkoutCatalogResponse toCatalogResponse(WorkoutCatalog entity) {
        if (entity == null)
            return null;
        WorkoutCatalogResponse response = new WorkoutCatalogResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setLevel(entity.getLevel());
        response.setType(entity.getType());
        response.setMuscleGroups(entity.getMuscleGroups());
        response.setEquipment(entity.getEquipment());
        response.setContraindications(entity.getContraindications());
        response.setActive(entity.getActive());
        return response;
    }

    // ===== Catalog: Request -> Entity (CREATE) =====
    public WorkoutCatalog toCatalogEntity(WorkoutCatalogRequest request) {
        WorkoutCatalog entity = new WorkoutCatalog();
        updateCatalogEntity(entity, request);
        return entity;
    }

    // ===== Catalog: Request -> Entity (UPDATE) =====
    public void updateCatalogEntity(WorkoutCatalog entity, WorkoutCatalogRequest request) {
        entity.setName(request.getName());
        entity.setLevel(request.getLevel());
        entity.setType(request.getType());
        entity.setMuscleGroups(request.getMuscleGroups());
        entity.setEquipment(request.getEquipment());
        entity.setContraindications(request.getContraindications());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }

    // ===== Plan: Entity -> Response =====
    public WorkoutPlanResponse toPlanResponse(WorkoutPlan plan) {
        if (plan == null)
            return null;
        WorkoutPlanResponse response = new WorkoutPlanResponse();
        response.setId(plan.getId());
        response.setAssessmentId(plan.getAssessment().getId());
        response.setDaysPerWeek(plan.getDaysPerWeek());
        response.setTotalWeeks(plan.getTotalWeeks());
        response.setStatus(plan.getStatus());

        if (plan.getDays() != null) {
            response.setDays(plan.getDays().stream()
                    .sorted(Comparator.comparingInt(WorkoutDay::getDayIndex))
                    .map(this::toDayResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    public WorkoutDayResponse toDayResponse(WorkoutDay day) {
        if (day == null)
            return null;
        WorkoutDayResponse response = new WorkoutDayResponse();
        response.setId(day.getId());
        response.setDayIndex(day.getDayIndex());
        response.setFocus(day.getFocus());
        response.setTotalDurationMin(day.getTotalDurationMin());
        response.setCaloriesEstimate(day.getCaloriesEstimate());

        if (day.getItems() != null) {
            response.setItems(day.getItems().stream()
                    .sorted(Comparator.comparingInt(WorkoutItem::getOrderIndex))
                    .map(this::toItemResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    public WorkoutItemResponse toItemResponse(WorkoutItem item) {
        if (item == null)
            return null;
        WorkoutItemResponse response = new WorkoutItemResponse();
        response.setId(item.getId());
        response.setCatalogId(item.getWorkoutCatalog().getId());
        response.setExerciseName(item.getWorkoutCatalog().getName());
        response.setOrderIndex(item.getOrderIndex());
        response.setSets(item.getSets());
        response.setReps(item.getReps());
        response.setDurationSec(item.getDurationSec());
        response.setRestSec(item.getRestSec());
        response.setNote(item.getNote());
        response.setCompleted(item.getCompleted());
        return response;
    }
}
