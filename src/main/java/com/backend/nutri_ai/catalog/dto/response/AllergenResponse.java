package com.backend.nutri_ai.catalog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AllergenResponse {
    private UUID id;
    private String code;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}