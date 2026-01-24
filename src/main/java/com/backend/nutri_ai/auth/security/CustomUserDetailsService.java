package com.backend.nutri_ai.auth.security;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.repository.UserRepository;
import com.backend.nutri_ai.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.engine.spi.Status;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {

        AppUser user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User blocked");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole())
                .build();
    }
}
