package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.catalog.dto.request.AllergenRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenResponse;
import com.backend.nutri_ai.catalog.service.IAllergenService;
import com.backend.nutri_ai.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/allergens")
@RequiredArgsConstructor
@Tag(name = "Admin Allergen Management", description = "Admin allergen CRUD operations")
public class AdminAllergenController {

    private final IAllergenService allergenService;

    @Operation(summary = "Get all allergens")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AllergenResponse>>> getAllAllergens(
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(allergenService.getAllAllergens(pageable)));
    }

    @Operation(summary = "Get allergen by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AllergenResponse>> getAllergenById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(allergenService.getAllergenById(id)));
    }

    @Operation(summary = "Create new allergen")
    @PostMapping
    public ResponseEntity<ApiResponse<AllergenResponse>> createAllergen(@Valid @RequestBody AllergenRequest request) {
        return new ResponseEntity<>(ApiResponse.ok("Allergen created", allergenService.createAllergen(request)),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update allergen")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AllergenResponse>> updateAllergen(
            @PathVariable UUID id,
            @Valid @RequestBody AllergenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Allergen updated", allergenService.updateAllergen(id, request)));
    }

    @Operation(summary = "Delete allergen")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAllergen(@PathVariable UUID id) {
        allergenService.deleteAllergen(id);
        return ResponseEntity.ok(ApiResponse.ok("Allergen deleted", null));
    }
}