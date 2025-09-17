package com.impulse.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuración de headers de seguridad para la aplicación
 * Implementa headers importantes como CSP, HSTS, X-Frame-Options, etc.
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /**
     * Filtro que añade headers de seguridad a todas las respuestas
     */
    @Bean
    public Filter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    /**
     * Filtro interno para aplicar headers de seguridad
     */
    private static class SecurityHeadersFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Content Security Policy - Previene ataques XSS
            httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://js.stripe.com; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                "font-src 'self' https://fonts.gstatic.com; " +
                "img-src 'self' data: https:; " +
                "connect-src 'self' https://api.stripe.com; " +
                "frame-src https://js.stripe.com https://hooks.stripe.com;"
            );

            // HTTP Strict Transport Security - Fuerza HTTPS
            httpResponse.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains; preload");

            // X-Frame-Options - Previene clickjacking
            httpResponse.setHeader("X-Frame-Options", "DENY");

            // X-Content-Type-Options - Previene MIME sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");

            // X-XSS-Protection - Protección XSS del navegador
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

            // Referrer Policy - Controla información del referrer
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Feature Policy - Controla APIs del navegador
            httpResponse.setHeader("Permissions-Policy",
                "camera=(), microphone=(), geolocation=(), payment=()");

            // Cache Control para contenido sensible
            if (httpRequest.getRequestURI() != null &&
                (httpRequest.getRequestURI().contains("/api/") ||
                 httpRequest.getRequestURI().contains("/admin/"))) {
                httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                httpResponse.setHeader("Pragma", "no-cache");
                httpResponse.setHeader("Expires", "0");
            }

            chain.doFilter(request, response);
        }
    }
}
