package com.backend.nutri_ai.auth.config;

import com.backend.nutri_ai.auth.security.JwtAuthenticationFilter;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ✅ bật CORS (dùng bean CorsConfigurationSource bên dưới)
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            ErrorCode ec = ErrorCode.UNAUTHORIZED;

                            response.setStatus(ec.getHttpStatus().value());
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.fail(ec.name(), ec.getMessage())
                            ));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ErrorCode ec = ErrorCode.FORBIDDEN;

                            response.setStatus(ec.getHttpStatus().value());
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.fail(ec.name(), ec.getMessage())
                            ));
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // ✅ cho preflight qua
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/auth/**","/api/users/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/dev/**",
                                "/api/admin/foods/**",
                                "/api/foods/**",
                                "/api/admin/workouts/**",
                                "/api/webhook/payos/**",
                                "/api/webhook/payos",
                                "/api/admin/**",
                                "/api/workouts/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ CORS config cho production
     * - Origin: https://fihealth.site
     * - Cho phép Authorization header để gửi JWT
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Nếu bạn KHÔNG dùng cookie thì có thể để false, nhưng để true cũng OK nếu bạn cần sau này
        config.setAllowCredentials(true);

        config.setAllowedOrigins(List.of(
                "https://fihealth.site"
                // Nếu bạn cần test local song song, thêm:
                // "http://localhost:5173"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

