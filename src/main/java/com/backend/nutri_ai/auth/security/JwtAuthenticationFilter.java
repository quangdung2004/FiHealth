package com.backend.nutri_ai.auth.security;

import com.backend.nutri_ai.auth.constant.SecurityConstant;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.AppUserRepository;
import com.backend.nutri_ai.common.enums.UserStatus;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserRepository userRepo;



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(SecurityConstant.AUTH_HEADER);

        // Không có token -> cho đi tiếp
        if (header == null || !header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(SecurityConstant.TOKEN_PREFIX.length());

        try {
            UUID userId = jwtService.extractUserId(token);
            Integer tokenVersion = jwtService.extractTokenVersion(token);

            AppUser user = userRepo
                    .findByIdAndStatusAndTokenVersion(
                            userId,
                            UserStatus.ACTIVE,
                            tokenVersion
                    )
                    .orElseThrow(() -> new DisabledException("User blocked"));

            var authentication = new UsernamePasswordAuthenticationToken(
                    user.getId(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        // ===== Chỉ các lỗi AUTH/JWT mới coi là 401 =====
        catch (JwtException | IllegalArgumentException | DisabledException e) {
            log.warn("JWT auth failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            // coi như chưa đăng nhập, để Spring Security xử lý
            filterChain.doFilter(request, response);
            return;
        }
        // ===== Lỗi hệ thống: cho nổ ra 500 =====
        catch (Exception e) {
            log.error("System error in JWT filter", e);
            throw new ServletException(e);
        }


        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/api/webhook/payos")   // ✅ ĐÚNG PATH
                || path.startsWith("/api/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }


}
