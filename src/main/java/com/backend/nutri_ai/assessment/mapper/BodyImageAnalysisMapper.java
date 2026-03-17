package com.backend.nutri_ai.assessment.mapper;

import com.backend.nutri_ai.ai.dto.BodyImageAiDto;
import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BodyImageAnalysisMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "assessment", source = "assessment")
    @Mapping(target = "rawJson", source = "rawJson")
    void update(@MappingTarget BodyImageAnalysis target, NutritionAssessment assessment, BodyImageAiDto ai, String rawJson);
}
