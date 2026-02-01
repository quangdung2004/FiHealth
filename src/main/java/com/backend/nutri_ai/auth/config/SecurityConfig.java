package com.backend.nutri_ai.auth.config;

import com.backend.nutri_ai.auth.security.JwtAuthenticationFilter;
import com.backend.nutri_ai.common.ApiResponse;
import com.backend.nutri_ai.common.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        // 401: chưa đăng nhập / token sai/hết hạn (không set Authentication)
                        .authenticationEntryPoint((request, response, authException) -> {
                            ErrorCode ec = ErrorCode.UNAUTHORIZED;

                            response.setStatus(ec.getHttpStatus().value());
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.fail(ec.name(), ec.getMessage())
                            ));
                        })
                        // 403: đã đăng nhập nhưng không đủ quyền
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
                        .requestMatchers(
                                "/api/auth/**","/api/users/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
//                                "/api/assessments/**",
                                "/api/dev/**",
                                "/api/admin/foods/**",
                                "/api/foods/**",
                                "/api/admin/workouts/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
