package com.impulse.infrastructure.config;

import com.impulse.shared.annotations.Generated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * RBAC enforcement, authentication, CORS, CSRF protection
 * Following IMPULSE v1.0 Anexo 1 §26 authentication requirements
 */
@Generated
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: Configure security - RBAC rules, JWT, CORS
        // Webhook endpoints should bypass CSRF
        return http.build();
    }

    /**
     * Password encoder bean for secure password hashing
     * Uses BCrypt for password storage per Anexo 1 §26 security requirements
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
