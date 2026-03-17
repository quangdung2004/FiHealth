package com.backend.nutri_ai.assessment.mapper;

import com.backend.nutri_ai.assessment.dto.BodyAnalysisResponse;
import com.backend.nutri_ai.assessment.entity.BodyImageAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BodyImageAnalysisResponseMapper {

    @Mapping(source = "assessment.id", target = "assessmentId")
    BodyAnalysisResponse toResponse(BodyImageAnalysis entity);
}
