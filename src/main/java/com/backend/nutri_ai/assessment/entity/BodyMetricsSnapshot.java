package com.backend.nutri_ai.assessment.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="body_metrics_snapshot")
@Getter @Setter
public class BodyMetricsSnapshot extends BaseEntity {

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assessment_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    @Column(nullable=false) private Double bmi;
    @Column(nullable=false) private Integer bmr;
    @Column(nullable=false) private Integer tdee;
    @Column(nullable=false) private Integer calorieTarget;

    @Column(nullable=false) private Integer proteinG;
    @Column(nullable=false) private Integer fatG;
    @Column(nullable=false) private Integer carbG;
}

