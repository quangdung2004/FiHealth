package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.AllergenRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAllergenService {

    Page<AllergenResponse> getAllAllergens(Pageable pageable);

    AllergenResponse getAllergenById(UUID id);

    AllergenResponse createAllergen(AllergenRequest request);

    AllergenResponse updateAllergen(UUID id, AllergenRequest request);

    void deleteAllergen(UUID id);
}