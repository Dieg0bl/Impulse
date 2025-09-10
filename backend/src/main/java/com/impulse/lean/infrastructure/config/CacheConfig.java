package com.impulse.lean.infrastructure.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Executor;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * IMPULSE LEAN v1 - Cache and Performance Configuration
 * 
 * Caffeine caching configuration for high-performance data access
 * Async processing configuration for background tasks
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Configuration
@EnableCaching
@EnableAsync
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats());
        
        // Specific cache configurations
        cacheManager.setCacheNames(Arrays.asList("users", "challenges", "evidences", "validators", "analytics", "system-config"));
        
        return cacheManager;
    }

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ImpulseLean-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
