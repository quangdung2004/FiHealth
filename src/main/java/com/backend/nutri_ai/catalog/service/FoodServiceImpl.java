package com.backend.nutri_ai.catalog.service;

import com.backend.nutri_ai.catalog.dto.request.FoodRequest;
import com.backend.nutri_ai.catalog.dto.response.AllergenSimpleResponse;
import com.backend.nutri_ai.catalog.dto.response.FoodResponse;
import com.backend.nutri_ai.catalog.entity.FoodItem;
import com.backend.nutri_ai.catalog.repository.IAllergenRepository;
import com.backend.nutri_ai.catalog.repository.IFoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final IFoodRepository foodRepository;
    private final IAllergenRepository allergenRepository;

    // ================= SEARCH =================
    @Override
    public Page<FoodResponse> searchFoods(String query, Pageable pageable) {
        return foodRepository.searchFoods(query, true, pageable)
                .map(this::mapToFoodResponse);
    }

    // ================= GET BY ID =================
    @Override
    public FoodResponse getFoodById(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
        return mapToFoodResponse(food);
    }

    // ================= CREATE =================
    @Override
    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        FoodItem food = new FoodItem();
        mapRequestToFood(request, food);

        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            food.setAllergens(new HashSet<>(
                    allergenRepository.findAllById(request.getAllergenIds())
            ));
        }

        return mapToFoodResponse(foodRepository.save(food));
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public FoodResponse updateFood(UUID id, FoodRequest request) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));

        mapRequestToFood(request, food);

        if (request.getAllergenIds() != null) {
            food.getAllergens().clear();
            if (!request.getAllergenIds().isEmpty()) {
                food.getAllergens().addAll(
                        allergenRepository.findAllById(request.getAllergenIds())
                );
            }
        }

        return mapToFoodResponse(foodRepository.save(food));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    @Transactional
    public void deleteFood(UUID id) {
        FoodItem food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
        food.setActive(false);
        foodRepository.save(food);
    }

    // ================= MAPPING =================
    private FoodResponse mapToFoodResponse(FoodItem food) {
        FoodResponse response = new FoodResponse();
        response.setId(food.getId());
        response.setName(food.getName());
        response.setBrand(food.getBrand());
        response.setServingSize(food.getServingSize());
        response.setKcalPerServing(food.getKcalPerServing());
        response.setProteinG(food.getProteinG());
        response.setFatG(food.getFatG());
        response.setCarbG(food.getCarbG());
        response.setEstimatedPriceVndPerServing(food.getEstimatedPriceVndPerServing());
        response.setTags(food.getTags());
        response.setActive(food.getActive());
        response.setCreatedAt(LocalDateTime.from(food.getCreatedAt()));
        response.setUpdatedAt(LocalDateTime.from(food.getUpdatedAt()));

        if (food.getAllergens() != null) {
            response.setAllergens(
                    food.getAllergens().stream()
                            .map(a -> {
                                AllergenSimpleResponse r = new AllergenSimpleResponse();
                                r.setId(a.getId());
                                r.setCode(a.getCode());
                                r.setName(a.getName());
                                return r;
                            })
                            .collect(Collectors.toSet())
            );
        }

        return response;
    }

    private void mapRequestToFood(FoodRequest request, FoodItem food) {
        food.setName(request.getName());
        food.setBrand(request.getBrand());
        food.setServingSize(request.getServingSize());
        food.setKcalPerServing(request.getKcalPerServing());
        food.setProteinG(request.getProteinG());
        food.setFatG(request.getFatG());
        food.setCarbG(request.getCarbG());
        food.setEstimatedPriceVndPerServing(request.getEstimatedPriceVndPerServing());
        food.setTags(request.getTags());

        if (request.getActive() != null) {
            food.setActive(request.getActive());
        }
    }
}
