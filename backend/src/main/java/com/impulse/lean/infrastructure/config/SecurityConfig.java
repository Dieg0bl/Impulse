package com.impulse.lean.infrastructure.config;

import com.impulse.lean.infrastructure.security.JwtAuthenticationFilter;
import com.impulse.lean.infrastructure.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * IMPULSE LEAN v1 - Security Configuration
 * 
 * JWT RS256 + RBAC Security Configuration
 * - Stateless authentication with JWT tokens
 * - Role-based access control (≤7 roles)
 * - CORS configuration for React frontend
 * - Rate limiting integration
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/docs/**", "/api/swagger-ui/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Moderator+ endpoints
                .requestMatchers("/api/moderation/**").hasAnyRole("MODERATOR", "ADMIN", "SUPER_ADMIN")
                
                // Validator+ endpoints
                .requestMatchers("/api/validations/**").hasAnyRole("VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/validations/**").hasAnyRole("VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                
                // Authenticated user endpoints
                .requestMatchers("/api/user/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/users/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/challenges/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/challenges/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/evidences/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/evidences/**").hasAnyRole("USER", "VALIDATOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://*.impulse-lean.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
