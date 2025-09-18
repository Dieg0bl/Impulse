package com.impulse.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.impulse.infrastructure.interceptors.RateLimitInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

/**
 * Configuración de Rate Limiting para la aplicación
 * Implementa rate limiting por IP usando Bucket4j según especificación IMPULSE v1.0 §6
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    @Bean
    public Bucket createBucket() {
        // 100 requests per minute per IP - aligned with v1.0 specification
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/status");
    }
}
