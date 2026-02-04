package com.backend.nutri_ai.auth.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegisterUser {
    private String email;
    private String fullName;
    private String passwordHash;
    private String role;
}