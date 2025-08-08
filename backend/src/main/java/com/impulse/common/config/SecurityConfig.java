package com.impulse.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.impulse.common.security.JwtAuthenticationFilter;
import com.impulse.common.security.JwtProvider;
import com.impulse.common.security.RBACFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    private final com.impulse.common.security.CustomAccessDeniedHandler accessDeniedHandler;
    private final com.impulse.common.security.CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(com.impulse.common.security.CustomAccessDeniedHandler accessDeniedHandler,
                          com.impulse.common.security.CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider, RBACFilter rbacFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Solo desactivar si usas JWT puro
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/backup/**").hasRole("BACKUP")
                .requestMatchers("/api/readonly/**").hasRole("SOLO_LECTURA")
                .requestMatchers("/api/demo/**", "/api/auth/**", "/api/public/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Solo en dev/test
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rbacFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Coste >=10
    }

    // Beans para JwtProvider y RBACFilter se definen en sus clases respectivas
}
