package com.impulse.lean.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para integración con Stripe
 * Define beans y configuraciones necesarias para billing
 */
@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key:sk_test_51234567890}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key:pk_test_51234567890}")
    private String stripePublishableKey;

    @Value("${stripe.webhook.secret:whsec_1234567890}")
    private String webhookSecret;

    @Value("${stripe.api.version:2023-10-16}")
    private String apiVersion;

    /**
     * Configuración de claves de Stripe
     */
    @Bean
    public StripeConfiguration stripeConfiguration() {
        StripeConfiguration config = new StripeConfiguration();
        config.setSecretKey(stripeSecretKey);
        config.setPublishableKey(stripePublishableKey);
        config.setWebhookSecret(webhookSecret);
        config.setApiVersion(apiVersion);
        return config;
    }

    /**
     * Configuración de precios de planes
     */
    @Bean
    public Map<String, PlanPricing> planPricingConfiguration() {
        Map<String, PlanPricing> pricing = new HashMap<>();

        // Plan FREE
        pricing.put("FREE", new PlanPricing("FREE", "Gratis", 
            java.math.BigDecimal.ZERO, "price_free"));

        // Plan PREMIUM
        pricing.put("PREMIUM", new PlanPricing("PREMIUM", "Premium", 
            new java.math.BigDecimal("9.99"), "price_premium_monthly"));

        // Plan PRO
        pricing.put("PRO", new PlanPricing("PRO", "Pro", 
            new java.math.BigDecimal("19.99"), "price_pro_monthly"));

        // Plan ENTERPRISE
        pricing.put("ENTERPRISE", new PlanPricing("ENTERPRISE", "Enterprise", 
            new java.math.BigDecimal("49.99"), "price_enterprise_monthly"));

        return pricing;
    }

    /**
     * Configuración de webhooks de Stripe
     */
    @Bean
    public StripeWebhookConfig stripeWebhookConfig() {
        StripeWebhookConfig config = new StripeWebhookConfig();
        config.setEndpointSecret(webhookSecret);
        config.setTolerance(300); // 5 minutos de tolerancia para timestamps
        return config;
    }

    /**
     * Clase para configuración de Stripe
     */
    public static class StripeConfiguration {
        private String secretKey;
        private String publishableKey;
        private String webhookSecret;
        private String apiVersion;

        // Getters y setters
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

        public String getPublishableKey() { return publishableKey; }
        public void setPublishableKey(String publishableKey) { this.publishableKey = publishableKey; }

        public String getWebhookSecret() { return webhookSecret; }
        public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }

        public String getApiVersion() { return apiVersion; }
        public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    }

    /**
     * Clase para configuración de precios de planes
     */
    public static class PlanPricing {
        private String planId;
        private String displayName;
        private java.math.BigDecimal monthlyPrice;
        private String stripePriceId;

        public PlanPricing() {}

        public PlanPricing(String planId, String displayName, 
                          java.math.BigDecimal monthlyPrice, String stripePriceId) {
            this.planId = planId;
            this.displayName = displayName;
            this.monthlyPrice = monthlyPrice;
            this.stripePriceId = stripePriceId;
        }

        // Getters y setters
        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public java.math.BigDecimal getMonthlyPrice() { return monthlyPrice; }
        public void setMonthlyPrice(java.math.BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }

        public String getStripePriceId() { return stripePriceId; }
        public void setStripePriceId(String stripePriceId) { this.stripePriceId = stripePriceId; }
    }

    /**
     * Clase para configuración de webhooks
     */
    public static class StripeWebhookConfig {
        private String endpointSecret;
        private long tolerance;

        // Getters y setters
        public String getEndpointSecret() { return endpointSecret; }
        public void setEndpointSecret(String endpointSecret) { this.endpointSecret = endpointSecret; }

        public long getTolerance() { return tolerance; }
        public void setTolerance(long tolerance) { this.tolerance = tolerance; }
    }
}
