package com.impulse.infrastructure.config;

import com.impulse.shared.annotations.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application Properties
 * Centralized configuration properties
 */
@Generated
@Configuration
@ConfigurationProperties(prefix = "impulse")
public class ApplicationProperties {

    // TODO: Define configuration properties
    // - Stripe API keys and webhook secrets
    // - File upload limits and storage configuration
    // - Rate limiting thresholds
    // - Feature flags configuration

    private String environment;
    private Stripe stripe = new Stripe();
    private Storage storage = new Storage();

    public static class Stripe {
        private String secretKey;
        private String webhookSecret;
        // TODO: Add getters/setters
    }

    public static class Storage {
        private String bucket;
        private long maxFileSize;
        // TODO: Add getters/setters
    }

    // TODO: Add getters/setters for all properties
}
