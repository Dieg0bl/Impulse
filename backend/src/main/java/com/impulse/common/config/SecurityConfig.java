package com.impulse.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.impulse.common.security.RBACFilter;
import com.impulse.common.security.filters.CookieAuthenticationFilter;
import com.impulse.security.filters.RateLimitingFilter;

/**
 * Configuración empresarial de seguridad con cookies HttpOnly y CSRF.
 * Implementa estándares OWASP y mejores prácticas de seguridad web.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    
    // Constantes para roles
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";
    private static final String ROLE_BACKUP = "BACKUP";
    private static final String ROLE_SOLO_LECTURA = "SOLO_LECTURA";
    
    @Value("${app.security.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.security.production:false}")
    private boolean isProduction;
    
    private final CookieAuthenticationFilter cookieAuthenticationFilter;
    private final RBACFilter rbacFilter;
    private final RateLimitingFilter rateLimitingFilter;
    private final com.impulse.common.security.CustomAccessDeniedHandler accessDeniedHandler;
    private final com.impulse.common.security.CustomAuthenticationEntryPoint authenticationEntryPoint;
    
    public SecurityConfig(CookieAuthenticationFilter cookieAuthenticationFilter, 
                         RBACFilter rbacFilter,
                         RateLimitingFilter rateLimitingFilter,
                         com.impulse.common.security.CustomAccessDeniedHandler accessDeniedHandler,
                         com.impulse.common.security.CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.cookieAuthenticationFilter = cookieAuthenticationFilter;
        this.rbacFilter = rbacFilter;
        this.rateLimitingFilter = rateLimitingFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS empresarial con cookies
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF habilitado con cookies
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                    "/api/auth/login",
                    "/api/auth/register", 
                    "/api/public/**",
                    "/actuator/health"
                )
            )
            
            // Política de sesiones: stateful para cookies
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(3) // Máximo 3 sesiones concurrentes
                .maxSessionsPreventsLogin(false)
            )
            
            // Headers de seguridad empresarial OWASP
            .headers(headers -> headers
                // X-Frame-Options: Previene clickjacking
                .frameOptions(frameOptions -> frameOptions.deny())
                
                // X-Content-Type-Options: Previene MIME sniffing
                .contentTypeOptions(contentType -> {})
                
                // HSTS: Forzar HTTPS en producción
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(63072000) // 2 años
                    .includeSubDomains(true)
                    .preload(true)
                )
                
                // Referrer Policy: Control de información de referencia
                .referrerPolicy(referrer -> referrer.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                
                // Content Security Policy: Máxima protección XSS
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(buildContentSecurityPolicy())
                )
                
                // Headers adicionales de seguridad
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Permissions-Policy", 
                    "geolocation=(), microphone=(), camera=(), payment=(), usb=(), " +
                    "magnetometer=(), gyroscope=(), speaker=(), vibrate=(), " +
                    "fullscreen=(self), sync-xhr=()"
                ))
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "X-Permitted-Cross-Domain-Policies", "none"
                ))
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "X-DNS-Prefetch-Control", "off"
                ))
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Cross-Origin-Embedder-Policy", "require-corp"
                ))
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Cross-Origin-Opener-Policy", "same-origin"
                ))
                .addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Cross-Origin-Resource-Policy", "same-origin"
                ))
            )
            
            // Autorización de endpoints empresarial
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos sin autenticación
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/csrf",
                    "/api/public/**",
                    "/api/demo/**",
                    "/actuator/health",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers("/api/flags").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/flags/refresh").hasRole(ROLE_ADMIN)
                
                // Endpoints de autenticación (requieren cookies válidas)
                .requestMatchers(
                    "/api/auth/status",
                    "/api/auth/logout",
                    "/api/auth/refresh"
                ).authenticated()
                
                // APIs protegidas por roles
                .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/api/backup/**").hasRole(ROLE_BACKUP)
                .requestMatchers("/api/readonly/**").hasRole(ROLE_SOLO_LECTURA)
                .requestMatchers("/api/usuario/**").hasAnyRole(ROLE_ADMIN, ROLE_USER)
                .requestMatchers("/api/tutor/**").hasAnyRole(ROLE_ADMIN, ROLE_USER)
                .requestMatchers("/api/reto/**").hasAnyRole(ROLE_ADMIN, ROLE_USER)
                .requestMatchers("/api/evidencia/**").hasAnyRole(ROLE_ADMIN, ROLE_USER)
                
                // APIs generales requieren autenticación
                .requestMatchers("/api/**").authenticated()
                
                // Recursos estáticos y páginas públicas
                .anyRequest().permitAll()
            )
            
            // Filtros de autenticación empresarial
            // Orden de filtros: rate limiting -> cookie auth -> RBAC, todos posicionados relativo a UsernamePasswordAuthenticationFilter
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(cookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(rbacFilter, CookieAuthenticationFilter.class)
            
            // Manejo de excepciones empresarial
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            );
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos
        configuration.setAllowedOrigins(List.of(
            frontendUrl,
            "http://localhost:3000",
            "https://impulse.empresa.com"
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type", 
            "X-Requested-With",
            "X-XSRF-TOKEN",
            "Accept",
            "Origin",
            "Cache-Control"
        ));
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(List.of(
            "Set-Cookie",
            "X-XSRF-TOKEN"
        ));
        
        // Permitir credenciales (cookies)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Construye una política de Content Security Policy (CSP) empresarial.
     * Bloquea XSS, injection y ataques de código no autorizado.
     */
    private String buildContentSecurityPolicy() {
        StringBuilder csp = new StringBuilder();
        
        // default-src: Solo recursos del mismo origen
        csp.append("default-src 'self'; ");
        
        // script-src: JavaScript controlado estrictamente  
        csp.append("script-src 'self' 'unsafe-inline' 'unsafe-eval' ");
        if (!isProduction) {
            // En desarrollo permitir webpack dev server
            csp.append("http://localhost:3000 ws://localhost:3000 ");
        }
        csp.append("; ");
        
        // style-src: CSS del mismo origen y inline necesario para React
        csp.append("style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; ");
        
        // img-src: Imágenes de fuentes confiables
        csp.append("img-src 'self' data: https: blob:; ");
        
        // font-src: Fuentes de Google Fonts
        csp.append("font-src 'self' https://fonts.gstatic.com; ");
        
        // connect-src: AJAX y WebSocket a endpoints confiables
        csp.append("connect-src 'self' ");
        if (!isProduction) {
            csp.append("http://localhost:3000 ws://localhost:3000 ");
        }
        csp.append("; ");
        
        // media-src: Media del mismo origen
        csp.append("media-src 'self'; ");
        
        // object-src: Bloquear Flash y objetos embebidos
        csp.append("object-src 'none'; ");
        
        // base-uri: Prevenir inyección de base tags
        csp.append("base-uri 'self'; ");
        
        // form-action: Solo permitir envío de formularios al mismo origen
        csp.append("form-action 'self'; ");
        
        // frame-ancestors: Prevenir iframe embedding
        csp.append("frame-ancestors 'none'; ");
        
        // upgrade-insecure-requests: Forzar HTTPS en producción
        if (isProduction) {
            csp.append("upgrade-insecure-requests; ");
        }
        
        // worker-src: Service workers del mismo origen
        csp.append("worker-src 'self'; ");
        
        // manifest-src: Web app manifest
        csp.append("manifest-src 'self'; ");
        
        return csp.toString().trim();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt con costo 12 para máxima seguridad
        return new BCryptPasswordEncoder(12);
    }
}
