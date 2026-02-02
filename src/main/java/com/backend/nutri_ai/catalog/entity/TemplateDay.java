package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name="template_day")
@Getter
@Setter
public class TemplateDay extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="template_id", nullable=false, columnDefinition="BINARY(16)")
    private MenuTemplate template;

    @Column(nullable=false)
    private Integer dayIndex;

    @OneToMany(mappedBy="day", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("mealOrder asc")
    private Set<TemplateMeal> meals = new LinkedHashSet<>();

}

