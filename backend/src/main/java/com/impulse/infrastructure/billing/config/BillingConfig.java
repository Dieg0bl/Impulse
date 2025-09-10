package com.impulse.infrastructure.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.stripe.Stripe;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

/**
 * Configuración de billing con toggles
 */
@Configuration
@ConditionalOnProperty(name = "impulse.billing.enabled", havingValue = "true")
public class BillingConfig {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;
    
    @Value("${stripe.publishable-key}")
    private String stripePublishableKey;
    
    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;
    
    @Value("${paypal.client-id}")
    private String paypalClientId;
    
    @Value("${paypal.client-secret}")
    private String paypalClientSecret;
    
    @Value("${paypal.environment:sandbox}")
    private String paypalEnvironment;

    @Bean
    public void configureStripe() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    @Bean
    public PayPalHttpClient paypalClient() {
        PayPalEnvironment environment;
        
        if ("production".equals(paypalEnvironment)) {
            environment = new PayPalEnvironment.Live(paypalClientId, paypalClientSecret);
        } else {
            environment = new PayPalEnvironment.Sandbox(paypalClientId, paypalClientSecret);
        }
        
        return new PayPalHttpClient(environment);
    }
    
    // Getters for configuration values
    public String getStripePublishableKey() {
        return stripePublishableKey;
    }
    
    public String getStripeWebhookSecret() {
        return stripeWebhookSecret;
    }
    
    public String getPaypalClientId() {
        return paypalClientId;
    }
    
    public boolean isPaypalProduction() {
        return "production".equals(paypalEnvironment);
    }
}
