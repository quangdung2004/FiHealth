package com.backend.nutri_ai.assessment.entity;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import com.backend.nutri_ai.common.enums.ImageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="uploaded_image", indexes = {
        @Index(name="idx_img_user", columnList="user_id")
})
@Getter @Setter
public class UploadedImage extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assessment_id", unique=true, columnDefinition="BINARY(16)")
    private NutritionAssessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ImageType type;

    @Column(nullable=false, length=500)
    private String storageUrl; // local/minio/s3

    private Long sizeBytes;

    @Column(length=120)
    private String contentType;
}

