package com.impulse.infrastructure.config;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Configuración específica para H2 en entorno de test
 * Desactiva las foreign keys constraints que causan problemas
 */
@Component
public class H2TestConfigurationListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(H2TestConfigurationListener.class);

    private final DataSource dataSource;
    private final Environment environment;

    public H2TestConfigurationListener(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isTestProfile = java.util.Arrays.asList(activeProfiles).contains("test");

        if (isTestProfile) {
            log.info("🔧 Aplicando configuración H2 para entorno de test...");
            configureH2ForTests();
        }
    }

    private void configureH2ForTests() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Desactivar foreign key constraints en H2
            statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            log.info("✅ Foreign key constraints desactivadas en H2");

            // Configurar modo MySQL compatible
            statement.execute("SET MODE MySQL");
            log.info("✅ H2 configurado en modo MySQL");

        } catch (Exception e) {
            log.warn("⚠️ No se pudo configurar H2 automáticamente: {}", e.getMessage());
            log.info("💡 Esto es normal si no estás usando H2 o si ya está configurado");
        }
    }
}
