package com.impulse.security.filters;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.security.EnterpriseRateLimiter;
import com.impulse.security.EnterpriseRateLimiter.EndpointType;
import com.impulse.security.EnterpriseRateLimiter.RateLimitInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de Rate Limiting que protege endpoints críticos automáticamente.
 * Aplica throttling inteligente basado en el tipo de endpoint y cliente.
 */
@Component
@Order(2) // Ejecutar después del filtro de autenticación
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final EnterpriseRateLimiter rateLimiter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public RateLimitingFilter(EnterpriseRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Determinar el tipo de endpoint
        EndpointType endpointType = determineEndpointType(path, method);
        
        if (endpointType != null) {
            String clientId = rateLimiter.extractClientId(request);
            
            // Verificar rate limit
            if (!rateLimiter.isAllowed(clientId, endpointType)) {
                handleRateLimitExceeded(request, response, clientId, endpointType);
                return;
            }
            
            // Agregar headers informativos
            addRateLimitHeaders(response, clientId, endpointType);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Determina el tipo de endpoint basado en la URL y método HTTP
     */
    private EndpointType determineEndpointType(String path, String method) {
        // Endpoints de autenticación críticos
        if (path.equals("/api/auth/login") && "POST".equals(method)) {
            return EndpointType.AUTH_LOGIN;
        }
        
        if (path.equals("/api/auth/register") && "POST".equals(method)) {
            return EndpointType.AUTH_REGISTER;
        }
        
        if (path.startsWith("/api/auth/password-reset")) {
            return EndpointType.PASSWORD_RESET;
        }
        
        // Endpoints de administración
        if (path.startsWith("/api/admin/")) {
            return EndpointType.API_ADMIN;
        }
        
        // Endpoints de upload/media
        if (path.contains("/upload") || path.contains("/media") || 
            (path.startsWith("/api/") && "POST".equals(method) && isUploadEndpoint(path))) {
            return EndpointType.API_UPLOAD;
        }
        
        // APIs generales
        if (path.startsWith("/api/")) {
            return EndpointType.API_GENERAL;
        }
        
        return null; // No aplicar rate limiting
    }
    
    /**
     * Detecta si un endpoint es de tipo upload basado en la URL
     */
    private boolean isUploadEndpoint(String path) {
        return path.contains("/evidencia") || 
               path.contains("/archivo") || 
               path.contains("/imagen") ||
               path.contains("/documento");
    }
    
    /**
     * Maneja cuando se excede el rate limit
     */
    private void handleRateLimitExceeded(
            HttpServletRequest request,
            HttpServletResponse response, 
            String clientId, 
            EndpointType endpointType) throws IOException {
        
        RateLimitInfo info = rateLimiter.getRateLimitInfo(clientId, endpointType);
        
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // Headers estándar de rate limiting
        response.setHeader("X-RateLimit-Limit", String.valueOf(info.getLimit()));
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Reset", 
            info.getResetTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setHeader("Retry-After", String.valueOf(
            java.time.Duration.between(java.time.LocalDateTime.now(), info.getResetTime()).getSeconds()));
        
        // Respuesta JSON informativa
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "RATE_LIMIT_EXCEEDED");
        errorResponse.put("message", "Demasiadas peticiones. Intenta de nuevo más tarde.");
        errorResponse.put("details", Map.of(
            "limit", info.getLimit(),
            "windowMinutes", endpointType.getWindowMinutes(),
            "resetTime", info.getResetTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "endpoint", endpointType.name()
        ));
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        
        // Log de seguridad
        if (logger.isWarnEnabled()) {
            logger.warn("Rate limit exceeded for client " + clientId + " on endpoint " + 
                       endpointType.name() + " (" + request.getRequestURI() + ")");
        }
    }
    
    /**
     * Agrega headers informativos sobre el estado del rate limit
     */
    private void addRateLimitHeaders(
            HttpServletResponse response, 
            String clientId, 
            EndpointType endpointType) {
        
        RateLimitInfo info = rateLimiter.getRateLimitInfo(clientId, endpointType);
        
        response.setHeader("X-RateLimit-Limit", String.valueOf(info.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(info.getRemaining()));
        response.setHeader("X-RateLimit-Reset", 
            info.getResetTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * Excluir ciertos paths del rate limiting
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Excluir health checks y recursos estáticos
        return path.startsWith("/actuator/health") ||
               path.startsWith("/static/") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/");
    }
}
