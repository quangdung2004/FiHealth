package com.backend.nutri_ai.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBugReportRequest {
    private String title;
    private String description;
    private String screen;
}

