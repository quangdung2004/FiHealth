package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.AllergenRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenResponse;
import com.backend.nutri_ai.catalog.entity.FoodAllergen;
import com.backend.nutri_ai.catalog.repository.IAllergenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllergenServicelmp implements IAllergenService {

    private final IAllergenRepository allergenRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AllergenResponse> getAllAllergens(Pageable pageable) {
        return allergenRepository.findAll(pageable)
                .map(this::mapToAllergenResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AllergenResponse getAllergenById(UUID id) {
        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allergen not found with id: " + id));
        return mapToAllergenResponse(allergen);
    }

    @Override
    @Transactional
    public AllergenResponse createAllergen(AllergenRequest request) {
        // Validate
        validateAllergenRequest(request);

        // Check if code already exists
        if (allergenRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new RuntimeException("Allergen with code " + request.getCode() + " already exists");
        }

        FoodAllergen allergen = new FoodAllergen();
        mapRequestToAllergen(request, allergen);

        FoodAllergen savedAllergen = allergenRepository.save(allergen);
        return mapToAllergenResponse(savedAllergen);
    }

    @Override
    @Transactional
    public AllergenResponse updateAllergen(UUID id, AllergenRequest request) {
        // Validate
        validateAllergenRequest(request);

        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allergen not found with id: " + id));

        String newCode = request.getCode().toUpperCase();
        String oldCode = allergen.getCode();

        // Check if new code conflicts with other allergens
        if (!oldCode.equals(newCode) && allergenRepository.existsByCode(newCode)) {
            throw new RuntimeException("Allergen with code " + newCode + " already exists");
        }

        mapRequestToAllergen(request, allergen);

        FoodAllergen updatedAllergen = allergenRepository.save(allergen);
        return mapToAllergenResponse(updatedAllergen);
    }

    @Override
    @Transactional
    public void deleteAllergen(UUID id) {
        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allergen not found with id: " + id));

        // Check if allergen is used by any food items
        if (!allergen.getFoodItems().isEmpty()) {
            int count = allergen.getFoodItems().size();
            throw new RuntimeException(
                    String.format("Cannot delete allergen '%s'. It is used by %d food item(s).",
                            allergen.getName(), count)
            );
        }

        allergenRepository.delete(allergen);
    }

    private void validateAllergenRequest(AllergenRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Allergen code is required");
        }

        if (!request.getCode().matches("^[A-Z_]+$")) {
            throw new RuntimeException("Allergen code must contain only uppercase letters and underscores");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Allergen name is required");
        }
    }

    private AllergenResponse mapToAllergenResponse(FoodAllergen allergen) {
        AllergenResponse response = new AllergenResponse();
        response.setId(allergen.getId());
        response.setCode(allergen.getCode());
        response.setName(allergen.getName());
        response.setCreatedAt(LocalDateTime.from(allergen.getCreatedAt()));
        response.setUpdatedAt(LocalDateTime.from(allergen.getUpdatedAt()));
        return response;
    }

    private void mapRequestToAllergen(AllergenRequest request, FoodAllergen allergen) {
        allergen.setCode(request.getCode().toUpperCase().trim());
        allergen.setName(request.getName().trim());
    }
}