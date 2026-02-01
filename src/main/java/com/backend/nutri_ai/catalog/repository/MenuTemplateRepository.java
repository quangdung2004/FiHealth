package com.backend.nutri_ai.catalog.repository;

import com.backend.nutri_ai.catalog.entity.MenuTemplate;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuTemplateRepository extends JpaRepository<MenuTemplate, UUID> {
    List<MenuTemplate> findByPeriod(PlanPeriod period);

    // chọn template tốt nhất theo budget gần nhất
    Optional<MenuTemplate> findTopByActiveTrueAndPeriodAndGoalAndBudgetPerDayVndLessThanEqualOrderByBudgetPerDayVndDesc(
            PlanPeriod period, Goal goal, Integer budgetPerDayVnd
    );

    // fetch full graph để tránh N+1
    @Query("""
        select t from MenuTemplate t
        left join fetch t.days d
        left join fetch d.meals m
        left join fetch m.items i
        left join fetch i.recipe r
        left join fetch i.foodItem f
        where t.id = :id
    """)
    Optional<MenuTemplate> findByIdWithGraph(@Param("id") UUID id);
}
