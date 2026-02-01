package com.backend.nutri_ai.admin.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="prompt_template", indexes = {@Index(name="idx_prompt_name", columnList="name")})
@Getter
@Setter
public class PromptTemplate extends BaseEntity {

    @Column(nullable=false, length=80)
    private String name; // MEAL_PLAN_JSON, BODY_ANALYZE, WORKOUT...

    @Column(nullable=false)
    private Integer version;

    @Lob @Column(columnDefinition="TEXT")
    private String systemPrompt;

    @Lob @Column(columnDefinition="TEXT")
    private String userPromptTemplate;

    @Column(nullable=false)
    private Boolean isDefault = false;

    @Column(nullable=false)
    private Boolean active = true;

    @Column(nullable=false, length=10)
    private String language = "vi";
}
