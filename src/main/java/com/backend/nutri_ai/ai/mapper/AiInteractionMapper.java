package com.backend.nutri_ai.ai.mapper;

import com.backend.nutri_ai.ai.entity.AiInteraction;
import com.backend.nutri_ai.common.enums.AiTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AiInteractionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assessmentId", source = "assessmentId")
    @Mapping(target = "task", source = "task")
    @Mapping(target = "prompt", source = "prompt")
    @Mapping(target = "response", source = "response")
    @Mapping(target = "latencyMs", source = "latencyMs")
    @Mapping(target = "model", source = "model")
    AiInteraction toEntity(UUID userId, UUID assessmentId, AiTask task, String prompt, String response, Integer latencyMs, String model);
}

