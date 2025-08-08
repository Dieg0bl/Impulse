package com.impulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Clase principal para arrancar la aplicación Spring Boot.
 * Cumple compliance: punto de entrada único, auditabilidad, trazabilidad.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ImpulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImpulseApplication.class, args);
    }
}
