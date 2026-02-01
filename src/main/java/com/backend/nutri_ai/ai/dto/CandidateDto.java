package com.backend.nutri_ai.ai.dto;

public record CandidateDto(
        String id,          // "R_<uuid>" or "F_<uuid>"
        String type,        // "RECIPE" | "FOOD"
        String name,
        int kcal,
        int proteinG,
        int fatG,
        int carbG,
        int costVnd,
        String servingUnit  // "1 serving" | food.servingSize
) {}
