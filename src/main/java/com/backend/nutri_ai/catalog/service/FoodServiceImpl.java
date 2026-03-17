package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.imports.FoodImportResult;
import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.mapper.FoodMapper;
import com.backend.nutri_ai.catalog.repository.IAllergenRepository;
import com.backend.nutri_ai.catalog.repository.IFoodRepository;
import com.backend.nutri_ai.common.enums.CatalogTag;
import com.backend.nutri_ai.common.exception.InvalidRequestException;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final IFoodRepository foodRepository;
    private final IAllergenRepository allergenRepository;
    private final FoodMapper foodMapper;

    @Override
    public Page<FoodResponse> searchFoods(String query, Pageable pageable) {
        Page<FoodItem> page = foodRepository.searchFoods(query, null, pageable);

        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No foods found matching query: " + query);
        }

        return page.map(foodMapper::toResponse);
    }

    @Override
    public FoodResponse getFoodById(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        return foodMapper.toResponse(food);
    }

    @Override
    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        if (foodRepository.existsByName(request.getName())) {
            throw new com.backend.nutri_ai.common.exception.DuplicatedResourceException(
                    "Food with name '" + request.getName() + "' already exists");
        }

        FoodItem food = foodMapper.toEntity(request);

        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            food.setAllergens(new HashSet<>(allergenRepository.findAllById(request.getAllergenIds())));
        }

        food = foodRepository.save(food);
        return foodMapper.toResponse(food);
    }

    @Override
    @Transactional
    public FoodResponse updateFood(UUID id, FoodRequest request) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        if (foodRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new com.backend.nutri_ai.common.exception.DuplicatedResourceException(
                    "Food with name '" + request.getName() + "' already exists");
        }

        foodMapper.updateEntity(food, request);

        if (request.getAllergenIds() != null) {
            food.getAllergens().clear();
            if (!request.getAllergenIds().isEmpty()) {
                food.getAllergens().addAll(allergenRepository.findAllById(request.getAllergenIds()));
            }
        }

        food = foodRepository.save(food);
        return foodMapper.toResponse(food);
    }

    @Override
    @Transactional
    public FoodImportResult importFoodsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Excel file is required");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            throw new InvalidRequestException("Only Excel files (.xlsx, .xls) are supported");
        }

        FoodImportResult result = FoodImportResult.builder().build();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                throw new InvalidRequestException("Excel file has no data rows");
            }

            Map<String, Integer> headers = readHeaders(sheet.getRow(0));
            validateRequiredHeaders(headers);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row)) {
                    continue;
                }
                result.setTotalRows(result.getTotalRows() + 1);
                try {
                    FoodRequest request = mapRowToFoodRequest(row, headers);
                    FoodResponse saved = createOrUpdateFoodForImport(request);
                    result.getImportedFoods().add(saved);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception ex) {
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.getErrors().add("Row " + (i + 1) + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new InvalidRequestException("Cannot read Excel file: " + e.getMessage());
        }

        return result;
    }

    private FoodResponse createOrUpdateFoodForImport(FoodRequest request) {
        Optional<FoodItem> existing = foodRepository.findByNameIgnoreCase(request.getName());
        if (existing.isPresent()) {
            return updateFood(existing.get().getId(), request);
        }
        return createFood(request);
    }

    private Map<String, Integer> readHeaders(Row headerRow) {
        Map<String, Integer> headers = new HashMap<>();
        for (Cell cell : headerRow) {
            headers.put(readCellAsString(cell).trim().toLowerCase(Locale.ROOT), cell.getColumnIndex());
        }
        return headers;
    }

    private void validateRequiredHeaders(Map<String, Integer> headers) {
        List<String> required = List.of("name", "servingsize", "kcalperserving", "proteing", "fatg", "carbg", "estimatedpricevndperserving");
        for (String header : required) {
            if (!headers.containsKey(header)) {
                throw new InvalidRequestException("Missing required column: " + header);
            }
        }
    }

    private FoodRequest mapRowToFoodRequest(Row row, Map<String, Integer> headers) {
        FoodRequest request = new FoodRequest();
        request.setName(requiredString(row, headers, "name"));
        request.setBrand(optionalString(row, headers, "brand"));
        request.setServingSize(requiredString(row, headers, "servingsize"));
        request.setKcalPerServing(requiredInteger(row, headers, "kcalperserving"));
        request.setProteinG(requiredInteger(row, headers, "proteing"));
        request.setFatG(requiredInteger(row, headers, "fatg"));
        request.setCarbG(requiredInteger(row, headers, "carbg"));
        request.setEstimatedPriceVndPerServing(requiredInteger(row, headers, "estimatedpricevndperserving"));
        request.setTags(optionalString(row, headers, "tags"));
        request.setTagEnums(parseTags(optionalString(row, headers, "tagenums")));
        request.setActive(parseBoolean(optionalString(row, headers, "active"), true));
        return request;
    }

    private Set<CatalogTag> parseTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LinkedHashSet<>();
        }
        return CatalogTag.fromCsv(raw);
    }

    private boolean parseBoolean(String raw, boolean defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw.trim());
    }

    private String requiredString(Row row, Map<String, Integer> headers, String key) {
        String value = optionalString(row, headers, key);
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("Column '" + key + "' is required");
        }
        return value;
    }

    private Integer requiredInteger(Row row, Map<String, Integer> headers, String key) {
        String value = optionalString(row, headers, key);
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("Column '" + key + "' is required");
        }
        try {
            return (int) Math.round(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Column '" + key + "' must be a number");
        }
    }

    private String optionalString(Row row, Map<String, Integer> headers, String key) {
        Integer columnIndex = headers.get(key);
        if (columnIndex == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return readCellAsString(cell);
    }

    private String readCellAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception ex) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && readCellAsString(cell) != null && !readCellAsString(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void deleteFood(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        foodRepository.delete(food);
    }
}
