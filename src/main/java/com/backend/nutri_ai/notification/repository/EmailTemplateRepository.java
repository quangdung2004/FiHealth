package com.backend.nutri_ai.notification.repository;

import com.backend.nutri_ai.notification.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailTemplateRepository
        extends JpaRepository<EmailTemplate, Long> {

    @Query("""
        SELECT e FROM EmailTemplate e
        WHERE e.code = :code AND e.active = true
        ORDER BY function('RAND')
        LIMIT 1
    """)
    Optional<EmailTemplate> randomByCode(String code);
}
