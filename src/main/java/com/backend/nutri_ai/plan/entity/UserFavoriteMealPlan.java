package com.backend.nutri_ai.plan.entity;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user_favorite_meal_plan",
        uniqueConstraints = @UniqueConstraint(name="uk_fav_user_plan", columnNames={"user_id","meal_plan_id"}),
        indexes = {
                @Index(name="idx_fav_plan", columnList="meal_plan_id"),
                @Index(name="idx_fav_user", columnList="user_id")
        })
@Getter @Setter
public class UserFavoriteMealPlan extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="meal_plan_id", nullable=false, columnDefinition="BINARY(16)")
    private MealPlan mealPlan;
}
