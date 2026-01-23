package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.FoodAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAllergenRepository extends JpaRepository<FoodAllergen, UUID> {

    Optional<FoodAllergen> findByCode(String code);

    boolean existsByCode(String code);
}