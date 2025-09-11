package com.impulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * IMPULSE LEAN v1 - Main Application
 * 
 * Challenge/Validation Platform with Full Monetization & Legal Compliance
 * Architecture: Hexagonal (Ports & Adapters)
 * Security: JWT RS256 + RBAC (≤7 roles)
 * Database: MySQL 8 with Flyway migrations
 * Frontend: React 18 + TypeScript PWA
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 * @since September 2025
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class ImpulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImpulseApplication.class, args);
    }
}
