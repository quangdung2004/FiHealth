package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IFoodService {

    Page<FoodResponse> searchFoods(String query, Pageable pageable);

    FoodResponse getFoodById(UUID id);

    FoodResponse createFood(FoodRequest request);

    FoodResponse updateFood(UUID id, FoodRequest request);

    void deleteFood(UUID id);
}