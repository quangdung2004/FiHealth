package com.backend.nutri_ai.auth.security;

import com.backend.nutri_ai.auth.config.JwtConfig;
import com.backend.nutri_ai.auth.constant.SecurityConstant;
import com.backend.nutri_ai.auth.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    /* ================= ACCESS TOKEN ================= */

    public String generateAccessToken(AppUser user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim(SecurityConstant.CLAIM_ROLE, user.getRole().name())
                .claim("tv", user.getTokenVersion()) // NOTE: tokenVersion
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration())
                )
                .signWith(signingKey())
                .compact();
    }

    /* ================= PARSE ================= */

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public String extractRole(String token) {
        return parse(token).get(SecurityConstant.CLAIM_ROLE, String.class);
    }

    public Integer extractTokenVersion(String token) {
        return parse(token).get("tv", Integer.class);
    }

    /* ================= REFRESH TOKEN ================= */

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
