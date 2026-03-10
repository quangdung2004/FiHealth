package com.backend.nutri_ai.catalog.controller;

import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.CatalogTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/catalog-tags")
public class AdminCatalogTagController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<CatalogTag>>> getAllTags() {
        return ResponseEntity.ok(ApiResponse.ok(Arrays.asList(CatalogTag.values())));
    }
}
