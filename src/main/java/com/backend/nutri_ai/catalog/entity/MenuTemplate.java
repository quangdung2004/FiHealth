package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.Goal;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="menu_template", indexes = {
        @Index(name="idx_template_active", columnList="active")
})
@Getter
@Setter
public class MenuTemplate extends BaseEntity {

    @Column(nullable=false, length=200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private PlanPeriod period; // DAY/WEEK

    @Column(nullable=false)
    private Integer budgetPerDayVnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Goal goal;

    @Column(length=500)
    private String tags;

    @Column(nullable=false)
    private Boolean active = true;

    @OneToMany(mappedBy="template", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("dayIndex asc")
    private Set<TemplateDay> days = new LinkedHashSet<>();

}

