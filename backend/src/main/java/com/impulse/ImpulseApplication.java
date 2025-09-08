package com.impulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;

/**
 * IMPULSE - Human Validation Platform
 * Clean hexagonal architecture implementation
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableCaching
public class ImpulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImpulseApplication.class, args);
    }
}
