package com.impulse.security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * Rate Limiter empresarial para proteger endpoints críticos.
 * Implementa throttling inteligente con diferentes niveles según el endpoint.
 */
@Component
public class EnterpriseRateLimiter {

    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    
    /**
     * Configuración de límites por tipo de endpoint
     */
    public enum EndpointType {
        AUTH_LOGIN(5, 15),          // 5 intentos por 15 minutos
        AUTH_REGISTER(3, 60),       // 3 registros por hora
        PASSWORD_RESET(2, 30),      // 2 resets por 30 minutos
        API_GENERAL(100, 1),        // 100 requests por minuto
        API_UPLOAD(10, 5),          // 10 uploads por 5 minutos
        API_ADMIN(50, 1);           // 50 admin requests por minuto
        
        private final int maxRequests;
        private final int windowMinutes;
        
        EndpointType(int maxRequests, int windowMinutes) {
            this.maxRequests = maxRequests;
            this.windowMinutes = windowMinutes;
        }
        
        public int getMaxRequests() { return maxRequests; }
        public int getWindowMinutes() { return windowMinutes; }
    }
    
    /**
     * Contador de requests con ventana deslizante
     */
    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile LocalDateTime windowStart;
        private final int windowMinutes;
        
        public RequestCounter(int windowMinutes) {
            this.windowMinutes = windowMinutes;
            this.windowStart = LocalDateTime.now();
        }
        
        public synchronized boolean allowRequest(int maxRequests) {
            LocalDateTime now = LocalDateTime.now();
            
            // Si ha pasado la ventana, resetear contador
            if (ChronoUnit.MINUTES.between(windowStart, now) >= windowMinutes) {
                count.set(0);
                windowStart = now;
            }
            
            // Verificar si se puede hacer la request
            int currentCount = count.get();
            if (currentCount < maxRequests) {
                count.incrementAndGet();
                return true;
            }
            
            return false;
        }
        
        public int getCurrentCount() {
            return count.get();
        }
        
        public LocalDateTime getWindowStart() {
            return windowStart;
        }
    }
    
    /**
     * Verifica si se permite una request para el cliente y endpoint dados
     */
    public boolean isAllowed(String clientId, EndpointType endpointType) {
        String key = clientId + ":" + endpointType.name();
        
        RequestCounter counter = requestCounts.computeIfAbsent(
            key, 
            k -> new RequestCounter(endpointType.getWindowMinutes())
        );
        
        return counter.allowRequest(endpointType.getMaxRequests());
    }
    
    /**
     * Obtiene información del estado del rate limit para un cliente
     */
    public RateLimitInfo getRateLimitInfo(String clientId, EndpointType endpointType) {
        String key = clientId + ":" + endpointType.name();
        RequestCounter counter = requestCounts.get(key);
        
        if (counter == null) {
            return new RateLimitInfo(
                endpointType.getMaxRequests(),
                0,
                endpointType.getMaxRequests(),
                LocalDateTime.now().plusMinutes(endpointType.getWindowMinutes())
            );
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetTime = counter.getWindowStart().plusMinutes(endpointType.getWindowMinutes());
        
        // Si ha pasado la ventana, mostrar límites limpios
        if (now.isAfter(resetTime)) {
            return new RateLimitInfo(
                endpointType.getMaxRequests(),
                0,
                endpointType.getMaxRequests(),
                now.plusMinutes(endpointType.getWindowMinutes())
            );
        }
        
        int used = counter.getCurrentCount();
        return new RateLimitInfo(
            endpointType.getMaxRequests(),
            used,
            endpointType.getMaxRequests() - used,
            resetTime
        );
    }
    
    /**
     * Extrae identificador del cliente desde la request
     */
    public String extractClientId(jakarta.servlet.http.HttpServletRequest request) {
        // Prioridad 1: Usuario autenticado
        if (request.getUserPrincipal() != null) {
            return "user:" + request.getUserPrincipal().getName();
        }
        
        // Prioridad 2: IP del cliente (considerando proxies)
        String clientIp = getClientIpAddress(request);
        return "ip:" + clientIp;
    }
    
    /**
     * Obtiene la IP real del cliente considerando proxies y load balancers
     */
    private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Tomar la primera IP de la cadena
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        String cfConnectingIp = request.getHeader("CF-Connecting-IP"); // Cloudflare
        if (cfConnectingIp != null && !cfConnectingIp.isEmpty()) {
            return cfConnectingIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Limpia contadores expirados para liberar memoria
     */
    public void cleanExpiredCounters() {
        LocalDateTime now = LocalDateTime.now();
        
        requestCounts.entrySet().removeIf(entry -> {
            RequestCounter counter = entry.getValue();
            LocalDateTime resetTime = counter.getWindowStart().plusMinutes(60); // Max window
            return now.isAfter(resetTime.plusMinutes(60)); // Extra buffer
        });
    }
    
    /**
     * Información del estado del rate limit
     */
    public static class RateLimitInfo {
        private final int limit;
        private final int used;
        private final int remaining;
        private final LocalDateTime resetTime;
        
        public RateLimitInfo(int limit, int used, int remaining, LocalDateTime resetTime) {
            this.limit = limit;
            this.used = used;
            this.remaining = remaining;
            this.resetTime = resetTime;
        }
        
        public int getLimit() { return limit; }
        public int getUsed() { return used; }
        public int getRemaining() { return remaining; }
        public LocalDateTime getResetTime() { return resetTime; }
        
        public boolean isExceeded() { return remaining <= 0; }
    }
}
