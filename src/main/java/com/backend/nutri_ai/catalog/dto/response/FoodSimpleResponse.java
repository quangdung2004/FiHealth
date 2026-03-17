package com.backend.nutri_ai.catalog.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class FoodSimpleResponse {
    private UUID id;
    private String name;
    private String servingSize;
    private Integer kcalPerServing;
    private Integer proteinG;
    private Integer fatG;
    private Integer carbG;
}