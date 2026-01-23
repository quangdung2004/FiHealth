package com.backend.nutri_ai.catalog.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class AllergenSimpleResponse {
    private UUID id;
    private String code;
    private String name;
}