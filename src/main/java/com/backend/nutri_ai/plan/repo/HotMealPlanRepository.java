package com.backend.nutri_ai.plan.repo;

import com.backend.nutri_ai.plan.entity.MealPlan;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface HotMealPlanRepository extends Repository<MealPlan, UUID> {

    @Query("""
        select mp
        from MealPlan mp
        left join UserFavoriteMealPlan fav on fav.mealPlan = mp
        where mp.period = :period
        group by mp
        order by count(fav.id) desc, mp.createdAt desc
    """)
    List<MealPlan> findHot(PlanPeriod period, Pageable pageable);
}
