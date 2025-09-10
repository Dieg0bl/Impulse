package com.impulse.monitoring;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sentry Integration para observabilidad
 * - Capture de errores automático
 * - Contexto de usuario
 * - Performance monitoring
 * - Breadcrumbs personalizados
 */
@Component
public class SentryMonitoringFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(SentryMonitoringFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // Generar correlation ID para traceabilidad
        String correlationId = UUID.randomUUID().toString();
        Sentry.setTag("correlation_id", correlationId);
        
        // Configurar contexto de usuario
        setupUserContext();
        
        // Configurar contexto de request
        setupRequestContext(req);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Agregar breadcrumb del inicio del request
            Sentry.addBreadcrumb(String.format("Request started: %s %s", 
                req.getMethod(), req.getRequestURI()));
            
            chain.doFilter(request, response);
            
            // Log de performance si es muy lenta
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 2000) { // Más de 2 segundos
                Sentry.addBreadcrumb(String.format("Slow request detected: %dms", duration), 
                    SentryLevel.WARNING);
            }
            
        } catch (Exception e) {
            // Capturar error con contexto completo
            captureException(e, req, res, correlationId);
            throw e;
            
        } finally {
            // Limpiar contexto
            clearSentryContext();
            
            // Log final con métricas
            long totalDuration = System.currentTimeMillis() - startTime;
            logRequestMetrics(req, res, totalDuration, correlationId);
        }
    }
    
    /**
     * Configura contexto de usuario en Sentry
     */
    private void setupUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            User user = new User();
            user.setId(auth.getName());
            user.setUsername(auth.getName());
            
            // Agregar roles como tags
            if (auth.getAuthorities() != null) {
                auth.getAuthorities().forEach(authority -> 
                    Sentry.setTag("user_role", authority.getAuthority()));
            }
            
            Sentry.setUser(user);
        }
    }
    
    /**
     * Configura contexto de request en Sentry
     */
    private void setupRequestContext(HttpServletRequest request) {
        // Tags básicos
        Sentry.setTag("request_method", request.getMethod());
        Sentry.setTag("request_uri", request.getRequestURI());
        Sentry.setTag("user_agent", request.getHeader("User-Agent"));
        Sentry.setTag("client_ip", getClientIpAddress(request));
        
        // Contexto adicional
        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("method", request.getMethod());
        requestContext.put("url", request.getRequestURL().toString());
        requestContext.put("query_string", request.getQueryString());
        requestContext.put("remote_addr", request.getRemoteAddr());
        requestContext.put("timestamp", Instant.now().toString());
        
        // Headers (filtrar sensitivos)
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        requestContext.put("headers", headers);
        
        Sentry.setContext("request", requestContext);
    }
    
    /**
     * Captura excepción con contexto completo
     */
    private void captureException(Exception e, HttpServletRequest request, 
                                 HttpServletResponse response, String correlationId) {
        
        // Agregar contexto del error
        Sentry.setTag("error_correlation_id", correlationId);
        Sentry.setTag("response_status", String.valueOf(response.getStatus()));
        
        // Contexto del error
        Map<String, Object> errorContext = new HashMap<>();
        errorContext.put("exception_class", e.getClass().getSimpleName());
        errorContext.put("message", e.getMessage());
        errorContext.put("timestamp", Instant.now().toString());
        errorContext.put("request_uri", request.getRequestURI());
        errorContext.put("request_method", request.getMethod());
        
        Sentry.setContext("error", errorContext);
        
        // Breadcrumb del error
        Sentry.addBreadcrumb(String.format("Exception occurred: %s - %s", 
            e.getClass().getSimpleName(), e.getMessage()), SentryLevel.ERROR);
        
        // Capturar en Sentry
        Sentry.captureException(e);
        
        // Log local también
        logger.error("Exception captured [{}]: {} - {}", correlationId, 
            e.getClass().getSimpleName(), e.getMessage(), e);
    }
    
    /**
     * Log de métricas del request
     */
    private void logRequestMetrics(HttpServletRequest request, HttpServletResponse response, 
                                  long duration, String correlationId) {
        
        // Performance monitoring
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("duration_ms", duration);
        metrics.put("status_code", response.getStatus());
        metrics.put("method", request.getMethod());
        metrics.put("uri", request.getRequestURI());
        metrics.put("correlation_id", correlationId);
        
        Sentry.setContext("performance", metrics);
        
        // Alertas por performance
        if (duration > 5000) {
            Sentry.addBreadcrumb("Very slow request: " + duration + "ms", SentryLevel.WARNING);
        }
        
        // Log estructurado
        logger.info("Request completed [{}]: {} {} - {} - {}ms", 
            correlationId, request.getMethod(), request.getRequestURI(), 
            response.getStatus(), duration);
    }
    
    /**
     * Extrae IP real del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Verifica si el header contiene información sensible
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("auth") || 
               lowerName.contains("token") || 
               lowerName.contains("key") || 
               lowerName.contains("secret") ||
               lowerName.contains("password") ||
               "cookie".equals(lowerName);
    }
    
    /**
     * Limpia contexto de Sentry
     */
    private void clearSentryContext() {
        Sentry.clearBreadcrumbs();
        Sentry.removeTag("correlation_id");
        Sentry.removeTag("request_method");
        Sentry.removeTag("request_uri");
        Sentry.removeTag("user_agent");
        Sentry.removeTag("client_ip");
    }
}
