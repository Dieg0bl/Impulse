package com.impulse.common.security;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Cache empresarial para tokens con limpieza automática y métricas de rendimiento.
 * Diseñado para alta concurrencia y entornos de producción.
 */
@Component
public class EnterpriseTokenCache {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseTokenCache.class);
    
    private final ConcurrentHashMap<String, RefreshTokenData> tokenStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
    
    // Métricas de rendimiento
    private volatile long hitCount = 0;
    private volatile long missCount = 0;
    private volatile long evictionCount = 0;
    
    @PostConstruct
    public void initialize() {
        // Limpieza automática cada 5 minutos
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 5, 5, TimeUnit.MINUTES);
        logger.info("EnterpriseTokenCache inicializado con limpieza automática cada 5 minutos");
    }
    
    @PreDestroy
    public void shutdown() {
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("EnterpriseTokenCache finalizado correctamente");
    }
    
    /**
     * Almacena un token con limpieza automática
     */
    public void put(String token, RefreshTokenData data) {
        if (token == null || data == null) {
            throw new IllegalArgumentException("Token y data no pueden ser null");
        }
        
        tokenStore.put(token, data);
        logger.debug("Token almacenado. Cache size: {}", tokenStore.size());
    }
    
    /**
     * Recupera y elimina un token (single-use)
     */
    public RefreshTokenData remove(String token) {
        if (token == null) {
            missCount++;
            return null;
        }
        
        RefreshTokenData data = tokenStore.remove(token);
        if (data != null) {
            hitCount++;
            logger.debug("Token recuperado y eliminado. Cache size: {}", tokenStore.size());
        } else {
            missCount++;
        }
        
        return data;
    }
    
    /**
     * Verifica si un token existe sin eliminarlo
     */
    public boolean containsToken(String token) {
        if (token == null) {
            return false;
        }
        
        RefreshTokenData data = tokenStore.get(token);
        if (data != null && !data.isExpired(LocalDateTime.now())) {
            hitCount++;
            return true;
        } else {
            missCount++;
            if (data != null) {
                // Token expirado, eliminarlo
                tokenStore.remove(token);
                evictionCount++;
            }
            return false;
        }
    }
    
    /**
     * Elimina un token específico
     */
    public boolean removeToken(String token) {
        if (token == null) {
            return false;
        }
        
        RefreshTokenData removed = tokenStore.remove(token);
        if (removed != null) {
            evictionCount++;
            logger.debug("Token eliminado manualmente. Cache size: {}", tokenStore.size());
            return true;
        }
        return false;
    }
    
    /**
     * Limpieza automática de tokens expirados
     */
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int initialSize = tokenStore.size();
        
        // Contar y eliminar tokens expirados
        int[] removedCount = {0};
        tokenStore.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired(now);
            if (expired) {
                evictionCount++;
                removedCount[0]++;
            }
            return expired;
        });
        
        if (removedCount[0] > 0) {
            logger.info("Limpieza automática: {} tokens expirados eliminados. Cache size: {} -> {}", 
                       removedCount[0], initialSize, tokenStore.size());
        }
    }
    
    /**
     * Obtiene métricas del cache
     */
    public CacheMetrics getMetrics() {
        return new CacheMetrics(
            tokenStore.size(),
            hitCount,
            missCount,
            evictionCount,
            hitCount + missCount > 0 ? (double) hitCount / (hitCount + missCount) : 0.0
        );
    }
    
    /**
     * Limpia todos los tokens (para testing o mantenimiento)
     */
    public void clear() {
        int size = tokenStore.size();
        tokenStore.clear();
        evictionCount += size;
        logger.warn("Cache completamente limpiado. {} tokens eliminados", size);
    }
    
    /**
     * Clase para métricas del cache
     */
    public static class CacheMetrics {
        public final int size;
        public final long hitCount;
        public final long missCount;
        public final long evictionCount;
        public final double hitRate;
        
        public CacheMetrics(int size, long hitCount, long missCount, long evictionCount, double hitRate) {
            this.size = size;
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.evictionCount = evictionCount;
            this.hitRate = hitRate;
        }
        
        @Override
        public String toString() {
            return String.format("CacheMetrics{size=%d, hits=%d, misses=%d, evictions=%d, hitRate=%.2f%%}", 
                               size, hitCount, missCount, evictionCount, hitRate * 100);
        }
    }
}
