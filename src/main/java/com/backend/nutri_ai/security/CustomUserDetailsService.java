//package com.backend.nutri_ai.security;
//
//import com.backend.nutri_ai.auth.entity.AppUser;
//import com.backend.nutri_ai.auth.repo.AppUserRepo;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final AppUserRepo appUserRepo;
//
//    public CustomUserDetailsService(AppUserRepo appUserRepo) {
//        this.appUserRepo = appUserRepo;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        AppUser user = appUserRepo.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//        return User.builder()
//                .username(user.getEmail())
//                .password(user.getPasswordHash())      // ✅ đúng field của bạn
//                .roles(user.getRole().name())          // ADMIN / USER
//                .disabled(user.getStatus().name().equals("BLOCKED")
//                        || user.getStatus().name().equals("DELETED"))
//                .build();
//    }
//}
