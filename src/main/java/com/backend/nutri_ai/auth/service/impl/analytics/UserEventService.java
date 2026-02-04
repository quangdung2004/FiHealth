package com.backend.nutri_ai.auth.service.impl.analytics;

import com.backend.nutri_ai.auth.entity.UserEvent;
import com.backend.nutri_ai.auth.repository.UserEventRepository;
import com.backend.nutri_ai.common.enums.UserEventType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventRepository repo;
    private final RequestContextProvider ctx;

    public void track(UserEventType type,
                      UUID userId,
                      boolean success,
                      String source,
                      String refId,
                      String metadataJson,
                      HttpServletRequest req) {

        UserEvent e = new UserEvent();
        e.setEventType(type);
        e.setUserId(userId);
        e.setSuccess(success);
        e.setSource(source);
        e.setRefId(refId);
        e.setMetadataJson(metadataJson);

        // ✅ FIX bắt buộc: set time
        e.setOccurredAt(Instant.now());

        if (req != null) {
            e.setIp(ctx.ip(req));
            e.setUserAgent(ctx.userAgent(req));
            e.setSessionId(ctx.sessionId(req)); // ✅ bạn có field này nhưng trước chưa set
        }

        repo.save(e);
    }
}
