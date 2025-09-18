package com.impulse.infrastructure.config;

import com.impulse.infrastructure.filters.CorrelationIdFilter;
import com.impulse.infrastructure.interceptors.IdempotencyInterceptor;
import com.impulse.shared.annotations.Generated;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers filters, interceptors, and CORS configuration
 */
@Generated
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IdempotencyInterceptor idempotencyInterceptor;

    public WebConfig(IdempotencyInterceptor idempotencyInterceptor) {
        this.idempotencyInterceptor = idempotencyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO: Configure interceptors
        // - Idempotency interceptor for POST endpoints
        // - Rate limiting interceptor
        registry.addInterceptor(idempotencyInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/webhooks/**");
    }

    // TODO: Configure CORS for frontend integration
}
