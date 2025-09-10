package com.impulse.security;

import com.impulse.configuration.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Interceptor
 * - Intercepta requests antes de llegar al controller
 * - Aplica diferentes límites según endpoint y usuario
 * - Headers informativos sobre rate limiting
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    
    private final RateLimitConfig rateLimitConfig;
    
    // Mapeo de endpoints a tipos de rate limit
    private final Map<String, RateLimitConfig.EndpointLimitType> endpointLimits = new ConcurrentHashMap<>();
    
    @Autowired
    public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
        initializeEndpointLimits();
    }
    
    /**
     * Configura los límites por endpoint
     */
    private void initializeEndpointLimits() {
        // Autenticación - límites estrictos
        endpointLimits.put("/api/auth/login", RateLimitConfig.EndpointLimitType.AUTH);
        endpointLimits.put("/api/auth/register", RateLimitConfig.EndpointLimitType.AUTH);
        endpointLimits.put("/api/auth/forgot-password", RateLimitConfig.EndpointLimitType.STRICT);
        
        // Creación de contenido - límites moderados
        endpointLimits.put("/api/challenges", RateLimitConfig.EndpointLimitType.CHALLENGE_CREATE);
        endpointLimits.put("/api/evidences/upload", RateLimitConfig.EndpointLimitType.UPLOAD);
        
        // Validaciones - límites moderados
        endpointLimits.put("/api/validations", RateLimitConfig.EndpointLimitType.VALIDATION);
        
        // API general - límites generosos
        endpointLimits.put("/api/", RateLimitConfig.EndpointLimitType.API);
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Solo aplicar rate limiting a requests HTTP normales
        if (!"HTTP".equals(request.getProtocol().substring(0, 4))) {
            return true;
        }
        
        String clientIp = getClientIpAddress(request);
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();
        
        // Obtener tipo de límite para este endpoint
        RateLimitConfig.EndpointLimitType limitType = determineLimitType(requestUri, httpMethod);
        
        // Obtener identificador para el bucket (IP o usuario)
        String identifier = getIdentifier(request, clientIp);
        
        // Obtener bucket apropiado
        Bucket bucket = rateLimitConfig.getBucketForEndpoint(identifier, limitType);
        
        // Intentar consumir un token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Request permitido - agregar headers informativos
            addRateLimitHeaders(response, probe, limitType);
            return true;
        } else {
            // Rate limit excedido
            handleRateLimitExceeded(response, probe, limitType, clientIp, requestUri);
            return false;
        }
    }
    
    /**
     * Determina qué tipo de rate limit aplicar según el endpoint
     */
    private RateLimitConfig.EndpointLimitType determineLimitType(String uri, String method) {
        // Buscar coincidencia exacta
        for (Map.Entry<String, RateLimitConfig.EndpointLimitType> entry : endpointLimits.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Casos especiales por método HTTP
        if ("POST".equals(method)) {
            return RateLimitConfig.EndpointLimitType.API;
        }
        
        // Default: límite general
        return RateLimitConfig.EndpointLimitType.GENERAL;
    }
    
    /**
     * Obtiene identificador único (preferir usuario sobre IP)
     */
    private String getIdentifier(HttpServletRequest request, String clientIp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "user:" + auth.getName();
        }
        
        return "ip:" + clientIp;
    }
    
    /**
     * Extrae IP real del cliente (considerando proxies)
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
     * Agrega headers informativos sobre rate limiting
     */
    private void addRateLimitHeaders(HttpServletResponse response, ConsumptionProbe probe, 
                                   RateLimitConfig.EndpointLimitType limitType) {
        response.setHeader("X-RateLimit-Limit", String.valueOf(limitType.getRequests()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
        
        if (probe.getRemainingTokens() == 0) {
            long refillInSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000L;
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + refillInSeconds));
        }
    }
    
    /**
     * Maneja cuando se excede el rate limit
     */
    private void handleRateLimitExceeded(HttpServletResponse response, ConsumptionProbe probe,
                                       RateLimitConfig.EndpointLimitType limitType, String clientIp, String uri) {
        
        long refillInSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000L;
        
        // Headers de rate limiting
        response.setHeader("X-RateLimit-Limit", String.valueOf(limitType.getRequests()));
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + refillInSeconds));
        response.setHeader("Retry-After", String.valueOf(refillInSeconds));
        
        // Log del evento
        logger.warn("Rate limit exceeded for IP: {} on endpoint: {} - Reset in {} seconds", 
                   clientIp, uri, refillInSeconds);
        
        // Respuesta HTTP 429
        response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT); // 429 Too Many Requests
        response.setContentType("application/json");
        
        try {
            String jsonResponse = String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Retry after %d seconds.\",\"retryAfter\":%d}",
                refillInSeconds, refillInSeconds
            );
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            logger.error("Error writing rate limit response", e);
        }
    }
}
