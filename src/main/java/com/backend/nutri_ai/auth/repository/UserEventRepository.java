package com.backend.nutri_ai.auth.repository;


import com.backend.nutri_ai.auth.entity.UserEvent;
import com.backend.nutri_ai.common.enums.UserEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    long countByEventTypeAndOccurredAtBetween(UserEventType type, Instant from, Instant to);

    @Query("""
        select count(distinct e.userId)
        from UserEvent e
        where e.eventType = :type
          and e.userId is not null
          and e.occurredAt between :from and :to
    """)
    long countDistinctUserIdByTypeAndRange(@Param("type") UserEventType type,
                                           @Param("from") Instant from,
                                           @Param("to") Instant to);

    @Query("""
        select count(e)
        from UserEvent e
        where e.eventType = :type
          and e.occurredAt between :from and :to
    """)
    long countEventsByTypeAndRange(@Param("type") UserEventType type,
                                   @Param("from") Instant from,
                                   @Param("to") Instant to);
}