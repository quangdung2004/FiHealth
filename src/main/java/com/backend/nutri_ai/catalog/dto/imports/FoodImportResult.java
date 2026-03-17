package com.backend.nutri_ai.catalog.dto.imports;

import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodImportResult {
    @Builder.Default
    private int totalRows = 0;
    @Builder.Default
    private int successCount = 0;
    @Builder.Default
    private int failedCount = 0;
    @Builder.Default
    private List<FoodResponse> importedFoods = new ArrayList<>();
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
