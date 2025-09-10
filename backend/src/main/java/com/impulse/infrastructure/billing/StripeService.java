package com.impulse.infrastructure.billing;

import com.impulse.domain.model.billing.SubscriptionPlan;
import com.impulse.domain.model.billing.UserSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para integración con Stripe
 * Mockea las llamadas a Stripe API para evitar dependencias externas
 */
@Service
@ConditionalOnProperty(name = "impulse.billing.enabled", havingValue = "true")
public class StripeService {

    @Value("${stripe.publishable-key}")
    private String publishableKey;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    
    @Value("${impulse.billing.trial-days:90}")
    private int trialDays;

    /**
     * Crear cliente en Stripe
     */
    public String createCustomer(Long userId, String email, String name) {
        // Mock implementation - en producción usaría Stripe.Customer.create()
        String customerId = "cus_mock_" + userId + "_" + System.currentTimeMillis();
        
        try {
            // Simular llamada a Stripe API
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("email", email);
            customerParams.put("name", name);
            customerParams.put("metadata", Map.of("user_id", userId.toString()));
            
            // Customer customer = Customer.create(customerParams);
            // return customer.getId();
            
            return customerId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe customer: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crear suscripción en Stripe
     */
    public String createSubscription(String customerId, SubscriptionPlan plan, 
                                   UserSubscription.BillingCycle cycle, boolean withTrial) {
        
        // Mock implementation
        String subscriptionId = "sub_mock_" + customerId + "_" + System.currentTimeMillis();
        
        try {
            String priceId = cycle == UserSubscription.BillingCycle.YEARLY ? 
                           plan.getStripeYearlyPriceId() : plan.getStripeMonthlyPriceId();
            
            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", customerId);
            subscriptionParams.put("items", new Object[]{
                Map.of("price", priceId)
            });
            
            if (withTrial) {
                subscriptionParams.put("trial_period_days", trialDays);
            }
            
            // Metadata para tracking
            subscriptionParams.put("metadata", Map.of(
                "plan_code", plan.getPlanCode(),
                "billing_cycle", cycle.toString()
            ));
            
            // Subscription subscription = Subscription.create(subscriptionParams);
            // return subscription.getId();
            
            return subscriptionId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe subscription: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crear sesión de checkout
     */
    public String createCheckoutSession(String customerId, SubscriptionPlan plan, 
                                      UserSubscription.BillingCycle cycle, 
                                      String successUrl, String cancelUrl) {
        
        // Mock implementation
        String sessionId = "cs_mock_" + customerId + "_" + System.currentTimeMillis();
        
        try {
            String priceId = cycle == UserSubscription.BillingCycle.YEARLY ? 
                           plan.getStripeYearlyPriceId() : plan.getStripeMonthlyPriceId();
            
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put("customer", customerId);
            sessionParams.put("payment_method_types", new String[]{"card"});
            sessionParams.put("mode", "subscription");
            sessionParams.put("line_items", new Object[]{
                Map.of(
                    "price", priceId,
                    "quantity", 1
                )
            });
            sessionParams.put("success_url", successUrl);
            sessionParams.put("cancel_url", cancelUrl);
            
            // Configurar trial si es nuevo usuario
            if (plan.getPlanCode().equals("PRO") || plan.getPlanCode().equals("TEAMS")) {
                Map<String, Object> subscriptionData = new HashMap<>();
                subscriptionData.put("trial_period_days", trialDays);
                sessionParams.put("subscription_data", subscriptionData);
            }
            
            // Session session = Session.create(sessionParams);
            // return session.getUrl();
            
            return "https://checkout.stripe.com/pay/" + sessionId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe checkout session: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cancelar suscripción
     */
    public boolean cancelSubscription(String subscriptionId, boolean immediately) {
        
        try {
            // Subscription subscription = Subscription.retrieve(subscriptionId);
            
            if (immediately) {
                // subscription.cancel();
                return true;
            } else {
                // Cancelar al final del período actual
                // subscription.update(Map.of("cancel_at_period_end", true));
                return true;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error canceling Stripe subscription: " + e.getMessage(), e);
        }
    }
    
    /**
     * Reactivar suscripción cancelada
     */
    public boolean reactivateSubscription(String subscriptionId) {
        
        try {
            // Subscription subscription = Subscription.retrieve(subscriptionId);
            // subscription.update(Map.of("cancel_at_period_end", false));
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("Error reactivating Stripe subscription: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualizar método de pago
     */
    public String createPaymentMethodUpdateSession(String customerId, String returnUrl) {
        
        // Mock implementation
        String sessionId = "bcs_mock_" + customerId + "_" + System.currentTimeMillis();
        
        try {
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put("customer", customerId);
            sessionParams.put("return_url", returnUrl);
            
            // BillingPortal.Session session = BillingPortal.Session.create(sessionParams);
            // return session.getUrl();
            
            return "https://billing.stripe.com/session/" + sessionId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating billing portal session: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validar webhook de Stripe
     */
    public Map<String, Object> validateWebhook(String payload, String sigHeader) {
        
        try {
            // Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            // return event.getDataObjectDeserializer().getObject().orElse(null);
            
            // Mock event data
            Map<String, Object> mockEvent = new HashMap<>();
            mockEvent.put("id", "evt_mock_" + System.currentTimeMillis());
            mockEvent.put("type", "invoice.payment_succeeded");
            mockEvent.put("data", Map.of(
                "object", Map.of(
                    "id", "in_mock_123",
                    "customer", "cus_mock_123",
                    "subscription", "sub_mock_123",
                    "amount_paid", 1299,
                    "currency", "eur"
                )
            ));
            
            return mockEvent;
            
        } catch (Exception e) {
            throw new RuntimeException("Error validating Stripe webhook: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener información de suscripción
     */
    public Optional<Map<String, Object>> getSubscriptionInfo(String subscriptionId) {
        
        try {
            // Subscription subscription = Subscription.retrieve(subscriptionId);
            
            // Mock subscription data
            Map<String, Object> subscriptionInfo = new HashMap<>();
            subscriptionInfo.put("id", subscriptionId);
            subscriptionInfo.put("status", "active");
            subscriptionInfo.put("current_period_start", System.currentTimeMillis() / 1000);
            subscriptionInfo.put("current_period_end", (System.currentTimeMillis() / 1000) + 2592000); // +30 days
            subscriptionInfo.put("cancel_at_period_end", false);
            
            return Optional.of(subscriptionInfo);
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Crear portal de facturación para el cliente
     */
    public String createCustomerPortalSession(String customerId, String returnUrl) {
        
        // Mock implementation
        String sessionId = "bps_mock_" + customerId + "_" + System.currentTimeMillis();
        
        try {
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put("customer", customerId);
            sessionParams.put("return_url", returnUrl);
            
            // BillingPortal.Session session = BillingPortal.Session.create(sessionParams);
            // return session.getUrl();
            
            return "https://billing.stripe.com/p/session/" + sessionId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating customer portal session: " + e.getMessage(), e);
        }
    }
    
    // Getters para configuración
    public String getPublishableKey() {
        return publishableKey;
    }
    
    public int getTrialDays() {
        return trialDays;
    }
}
