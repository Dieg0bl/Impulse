package com.impulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal para arrancar la aplicación Spring Boot.
 * Cumple compliance: punto de entrada único, auditabilidad, trazabilidad.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableScheduling
public class ImpulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImpulseApplication.class, args);
    }
}
