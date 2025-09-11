package com.impulse.lean.infrastructure.config;

import java.util.Arrays;

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

import com.impulse.lean.infrastructure.security.CorrelationIdFilter;
import com.impulse.lean.infrastructure.security.JwtAuthenticationEntryPoint;
import com.impulse.lean.infrastructure.security.JwtAuthenticationFilter;
import com.impulse.lean.infrastructure.security.RateLimitFilter;
import com.impulse.lean.infrastructure.security.SecurityHeadersFilter;

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

    // Role constants to avoid string literal duplication
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MODERATOR = "MODERATOR";
    private static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    private static final String ROLE_VALIDATOR = "VALIDATOR";
    private static final String ROLE_USER = "USER";

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                         CorrelationIdFilter correlationIdFilter,
                         SecurityHeadersFilter securityHeadersFilter,
                         RateLimitFilter rateLimitFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.correlationIdFilter = correlationIdFilter;
        this.securityHeadersFilter = securityHeadersFilter;
        this.rateLimitFilter = rateLimitFilter;
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
                .requestMatchers("/api/v1/challenges/**").permitAll()
                .requestMatchers("/api/docs/**", "/api/swagger-ui/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/actuator/**").hasRole(ROLE_ADMIN)
                
                // Moderator+ endpoints
                .requestMatchers("/api/moderation/**").hasAnyRole(ROLE_MODERATOR, ROLE_ADMIN, ROLE_SUPER_ADMIN)
                
                // Validator+ endpoints
                .requestMatchers("/api/validations/**").hasAnyRole(ROLE_VALIDATOR, ROLE_MODERATOR, ROLE_ADMIN, ROLE_SUPER_ADMIN)
                
                // Authenticated user endpoints
                .requestMatchers("/api/user/**").hasAnyRole(ROLE_USER, ROLE_VALIDATOR, ROLE_MODERATOR, ROLE_ADMIN, ROLE_SUPER_ADMIN)
                .requestMatchers("/api/challenges/**").hasAnyRole(ROLE_USER, ROLE_VALIDATOR, ROLE_MODERATOR, ROLE_ADMIN, ROLE_SUPER_ADMIN)
                .requestMatchers("/api/evidences/**").hasAnyRole(ROLE_USER, ROLE_VALIDATOR, ROLE_MODERATOR, ROLE_ADMIN, ROLE_SUPER_ADMIN)
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            // Correlation id -> Rate limit -> Security headers -> JWT auth
            .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
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
