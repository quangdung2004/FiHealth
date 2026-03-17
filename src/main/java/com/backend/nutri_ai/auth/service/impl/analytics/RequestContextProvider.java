package com.backend.nutri_ai.auth.service.impl.analytics;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestContextProvider {

    public String ip(HttpServletRequest req) {
        if (req == null) return null;

        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();

        String xrip = req.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();

        return req.getRemoteAddr();
    }

    public String userAgent(HttpServletRequest req) {
        if (req == null) return null;
        return req.getHeader("User-Agent");
    }

    public String sessionId(HttpServletRequest req) {
        if (req == null) return null;
        var s = req.getSession(false);
        return s == null ? null : s.getId();
    }
}
