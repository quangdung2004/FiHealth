package com.backend.nutri_ai.assessment.mapper;

import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.assessment.entity.UploadedImage;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.ImageType;
import com.backend.nutri_ai.common.storage.StoredFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = ImageType.class
)
public interface UploadedImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "assessment", source = "assessment")
    @Mapping(target = "type", expression = "java(ImageType.BODY)")
    @Mapping(target = "storageUrl", source = "stored.path")
    //@Mapping(target = "originalFilename", source = "stored.originalFilename")
    @Mapping(target = "contentType", source = "stored.contentType")
    @Mapping(target = "sizeBytes", expression = "java(stored.getSizeBytes())")
    UploadedImage toEntity(AppUser user, NutritionAssessment assessment, StoredFile stored);
}
