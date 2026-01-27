package com.backend.nutri_ai.auth.constant;
public final class   SecurityConstant {

    private SecurityConstant() {}

    // ===== HEADER =====
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // ===== CLAIM =====
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_USER_ID = "userId";

    // ===== ROLE =====
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // ===== AUTH =====
    public static final String ANONYMOUS = "anonymousUser";
    public static final long REFRESH_TOKEN_TTL_SECONDS = 1209600;
}

