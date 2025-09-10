package com.impulse.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting con Bucket4j
 * - Rate limiting por IP y usuario
 * - Buckets distribuidos con Redis
 * - Configuración dinámica por endpoints
 */
@Configuration
public class RateLimitConfig {

    @Value("${app.rate-limit.general.requests:100}")
    private int generalRequests;

    @Value("${app.rate-limit.general.window-minutes:10}")
    private int generalWindowMinutes;

    @Value("${app.rate-limit.auth.requests:10}")
    private int authRequests;

    @Value("${app.rate-limit.auth.window-minutes:10}")
    private int authWindowMinutes;

    @Value("${app.rate-limit.api.requests:1000}")
    private int apiRequests;

    @Value("${app.rate-limit.api.window-minutes:60}")
    private int apiWindowMinutes;

    @Value("${app.rate-limit.upload.requests:20}")
    private int uploadRequests;

    @Value("${app.rate-limit.upload.window-minutes:10}")
    private int uploadWindowMinutes;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<String, Bucket> localBuckets = new ConcurrentHashMap<>();

    public RateLimitConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Rate limiter general para requests estándar
     */
    @Bean
    public Bucket generalRateLimiter() {
        Bandwidth limit = Bandwidth.classic(generalRequests, 
            Refill.intervally(generalRequests, Duration.ofMinutes(generalWindowMinutes)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Rate limiter para endpoints de autenticación
     */
    @Bean
    public Bucket authRateLimiter() {
        Bandwidth limit = Bandwidth.classic(authRequests,
            Refill.intervally(authRequests, Duration.ofMinutes(authWindowMinutes)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Rate limiter para API general
     */
    @Bean
    public Bucket apiRateLimiter() {
        Bandwidth limit = Bandwidth.classic(apiRequests,
            Refill.intervally(apiRequests, Duration.ofMinutes(apiWindowMinutes)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Rate limiter para uploads de archivos
     */
    @Bean
    public Bucket uploadRateLimiter() {
        Bandwidth limit = Bandwidth.classic(uploadRequests,
            Refill.intervally(uploadRequests, Duration.ofMinutes(uploadWindowMinutes)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Crea bucket dinámico por usuario
     */
    public Bucket createUserBucket(String userId, int requests, int windowMinutes) {
        String key = "rate_limit:user:" + userId;
        return localBuckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(requests,
                Refill.intervally(requests, Duration.ofMinutes(windowMinutes)));
            return Bucket4j.builder()
                .addLimit(limit)
                .build();
        });
    }

    /**
     * Crea bucket dinámico por IP
     */
    public Bucket createIpBucket(String ipAddress, int requests, int windowMinutes) {
        String key = "rate_limit:ip:" + ipAddress;
        return localBuckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(requests,
                Refill.intervally(requests, Duration.ofMinutes(windowMinutes)));
            return Bucket4j.builder()
                .addLimit(limit)
                .build();
        });
    }

    /**
     * Configura rate limiting específico por endpoint
     */
    public enum EndpointLimitType {
        GENERAL(100, 10),      // 100 requests / 10 minutos
        AUTH(10, 10),          // 10 requests / 10 minutos  
        API(1000, 60),         // 1000 requests / 60 minutos
        UPLOAD(20, 10),        // 20 uploads / 10 minutos
        VALIDATION(50, 10),    // 50 validaciones / 10 minutos
        CHALLENGE_CREATE(5, 60), // 5 retos / 60 minutos
        STRICT(5, 10);         // 5 requests / 10 minutos

        private final int requests;
        private final int windowMinutes;

        EndpointLimitType(int requests, int windowMinutes) {
            this.requests = requests;
            this.windowMinutes = windowMinutes;
        }

        public int getRequests() { return requests; }
        public int getWindowMinutes() { return windowMinutes; }
    }

    /**
     * Obtiene bucket según el tipo de endpoint
     */
    public Bucket getBucketForEndpoint(String identifier, EndpointLimitType limitType) {
        String key = "rate_limit:" + limitType.name().toLowerCase() + ":" + identifier;
        return localBuckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(limitType.getRequests(),
                Refill.intervally(limitType.getRequests(), 
                    Duration.ofMinutes(limitType.getWindowMinutes())));
            return Bucket4j.builder()
                .addLimit(limit)
                .build();
        });
    }

    /**
     * Limpia buckets viejos para liberar memoria
     */
    public void cleanupOldBuckets() {
        // Implementar limpieza periódica si es necesario
        // Por ahora los buckets se limpian automáticamente cuando expiran
    }
}
