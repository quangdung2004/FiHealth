package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.FoodItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FoodRepositoryImp {

    private final IFoodRepository foodRepository;

    public Page<FoodItem> searchFoods(String query, Pageable pageable) {
        return foodRepository.findByNameOrTagsContaining(query, pageable);
    }
}