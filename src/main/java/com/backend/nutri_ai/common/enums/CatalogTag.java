package com.backend.nutri_ai.common.enums;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public enum CatalogTag {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    DRINK,
    VEGETARIAN,
    VEGAN,
    LOW_CARB,
    HIGH_PROTEIN,
    LOW_FAT,
    LOW_SUGAR,
    HIGH_FIBER,
    KETO,
    BULKING,
    CUTTING,
    WEIGHT_LOSS,
    WEIGHT_GAIN,
    HEALTHY,
    QUICK,
    EASY_COOK,
    TRADITIONAL,
    KID_FRIENDLY,
    GLUTEN_FREE,
    DAIRY_FREE;

    public static CatalogTag fromValue(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Tag cannot be blank");
        }
        String normalized = normalize(raw);
        for (CatalogTag tag : values()) {
            if (tag.name().equals(normalized)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid tag: " + raw);
    }

    public static String toCsv(Collection<CatalogTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.stream().map(Enum::name).distinct().collect(Collectors.joining(","));
    }

    public static Set<CatalogTag> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(CatalogTag::fromValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static String normalize(String value) {
        String withoutAccent = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccent.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }
}
