package com.backend.nutri_ai.auth.dto.request.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailRequest {

    private String to;
    private String subject;
    private String content;
}