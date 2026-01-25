package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.AllergenRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenResponse;
import com.backend.nutri_ai.catalog.entity.FoodAllergen;
import com.backend.nutri_ai.catalog.mapper.AllergenMapper;
import com.backend.nutri_ai.catalog.repository.IAllergenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllergenServicelmp implements IAllergenService {

    private final IAllergenRepository allergenRepository;
    private final AllergenMapper allergenMapper;

    // ================= GET ALL =================
    @Override
    @Transactional(readOnly = true)
    public Page<AllergenResponse> getAllAllergens(Pageable pageable) {
        return allergenRepository.findAll(pageable)
                .map(allergenMapper::toResponse);
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public AllergenResponse getAllergenById(UUID id) {
        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Allergen not found with id: " + id)
                );

        return allergenMapper.toResponse(allergen);
    }

    // ================= CREATE =================
    @Override
    @Transactional
    public AllergenResponse createAllergen(AllergenRequest request) {
        validateAllergenRequest(request);

        String code = request.getCode().toUpperCase().trim();
        if (allergenRepository.existsByCode(code)) {
            throw new RuntimeException("Allergen with code " + code + " already exists");
        }

        FoodAllergen allergen = new FoodAllergen();
        allergenMapper.updateEntity(allergen, request);

        allergenRepository.save(allergen);
        return allergenMapper.toResponse(allergen);
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public AllergenResponse updateAllergen(UUID id, AllergenRequest request) {
        validateAllergenRequest(request);

        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Allergen not found with id: " + id)
                );

        String newCode = request.getCode().toUpperCase().trim();
        String oldCode = allergen.getCode();

        if (!oldCode.equals(newCode) && allergenRepository.existsByCode(newCode)) {
            throw new RuntimeException("Allergen with code " + newCode + " already exists");
        }

        allergenMapper.updateEntity(allergen, request);

        allergenRepository.save(allergen);
        return allergenMapper.toResponse(allergen);
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteAllergen(UUID id) {
        FoodAllergen allergen = allergenRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Allergen not found with id: " + id)
                );

        if (!allergen.getFoodItems().isEmpty()) {
            throw new RuntimeException(
                    String.format(
                            "Cannot delete allergen '%s'. It is used by %d food item(s).",
                            allergen.getName(),
                            allergen.getFoodItems().size()
                    )
            );
        }

        allergenRepository.delete(allergen);
    }

    // ================= VALIDATION =================
    private void validateAllergenRequest(AllergenRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Allergen code is required");
        }

        if (!request.getCode().matches("^[A-Z_]+$")) {
            throw new RuntimeException(
                    "Allergen code must contain only uppercase letters and underscores"
            );
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Allergen name is required");
        }
    }
}
