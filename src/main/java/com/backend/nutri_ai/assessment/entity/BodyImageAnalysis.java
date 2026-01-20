package com.backend.nutri_ai.assessment.entity;

import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="body_image_analysis")
@Getter @Setter
public class BodyImageAnalysis extends BaseEntity {

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assessment_id", nullable=false, unique=true, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    // chỉ nên để dạng "estimate" + note an toàn
    private Double bodyFatMin;
    private Double bodyFatMax;

    @Column(length=1000)
    private String postureNotes;

    @Column(length=1000)
    private String proportionsNotes;

    @Column(length=1000)
    private String safetyNotes;

    @Lob
    @Column(columnDefinition="TEXT")
    private String rawJson; // lưu output gốc từ AI để debug
}

