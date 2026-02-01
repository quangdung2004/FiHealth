package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.mapper.FoodMapper;
import com.backend.nutri_ai.catalog.repository.IAllergenRepository;
import com.backend.nutri_ai.catalog.repository.IFoodRepository;
import com.backend.nutri_ai.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final IFoodRepository foodRepository;
    private final IAllergenRepository allergenRepository;
    private final FoodMapper foodMapper;

    // ================= SEARCH =================
    @Override
    public Page<FoodResponse> searchFoods(String query, Pageable pageable) {
        Page<FoodItem> page = foodRepository.searchFoods(query, true, pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No foods found matching query: " + query);
        }
        return page.map(foodMapper::toResponse);
    }

    // ================= GET BY ID =================
    @Override
    public FoodResponse getFoodById(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        return foodMapper.toResponse(food);
    }

    // ================= CREATE =================
    @Override
    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        if (foodRepository.existsByName(request.getName())) {
            throw new com.backend.nutri_ai.common.exception.DuplicatedResourceException(
                    "Food with name '" + request.getName() + "' already exists");
        }

        // map request -> entity
        FoodItem food = foodMapper.toEntity(request);

        // xử lý allergen (business logic)
        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            food.setAllergens(
                    new HashSet<>(allergenRepository.findAllById(request.getAllergenIds())));
        }

        food = foodRepository.save(food);

        return foodMapper.toResponse(food);
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public FoodResponse updateFood(UUID id, FoodRequest request) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        // map request -> entity (update)
        foodMapper.updateEntity(food, request);

        // cập nhật allergens
        if (request.getAllergenIds() != null) {
            food.getAllergens().clear();

            if (!request.getAllergenIds().isEmpty()) {
                food.getAllergens().addAll(
                        allergenRepository.findAllById(request.getAllergenIds()));
            }
        }

        food = foodRepository.save(food);

        return foodMapper.toResponse(food);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    @Transactional
    public void deleteFood(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        food.setActive(false);
        foodRepository.save(food);
    }
}
