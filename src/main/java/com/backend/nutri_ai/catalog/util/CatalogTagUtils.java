package com.backend.nutri_ai.catalog.util;

import com.backend.nutri_ai.common.enums.CatalogTag;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class CatalogTagUtils {

    private CatalogTagUtils() {
    }

    public static String normalizeTags(String rawTags, Collection<CatalogTag> enumTags) {
        Set<CatalogTag> merged = new LinkedHashSet<>();
        if (rawTags != null && !rawTags.isBlank()) {
            merged.addAll(CatalogTag.fromCsv(rawTags));
        }
        if (enumTags != null && !enumTags.isEmpty()) {
            merged.addAll(enumTags);
        }
        return CatalogTag.toCsv(merged);
    }
}
