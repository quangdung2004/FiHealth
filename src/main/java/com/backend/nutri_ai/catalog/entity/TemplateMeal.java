package com.backend.nutri_ai.catalog.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="template_meal")
@Getter
@Setter
public class TemplateMeal extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="day_id", nullable=false, columnDefinition="BINARY(16)")
    private TemplateDay day;

    @Column(nullable=false)
    private Integer mealOrder;

    @Column(nullable=false, length=50)
    private String name; // Sáng/Trưa/Tối/Phụ

    @OneToMany(mappedBy="meal", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<TemplateItem> items = new ArrayList<>();
}

