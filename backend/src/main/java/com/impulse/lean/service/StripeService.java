package com.impulse.lean.service;

import com.impulse.lean.domain.User;
import com.impulse.lean.domain.Subscription;
import com.impulse.lean.domain.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para integración con Stripe API
 * Maneja customers, payment intents, subscriptions y webhooks
 */
@Service
public class StripeService {

    @Value("${stripe.secret.key:sk_test_default}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key:pk_test_default}")
    private String stripePublishableKey;

    @Value("${stripe.webhook.secret:whsec_default}")
    private String webhookSecret;

    /**
     * Crear customer en Stripe
     */
    public String createStripeCustomer(User user) {
        try {
            // Simular creación de customer en Stripe
            // En implementación real: usar Stripe.setApiKey() y Customer.create()
            
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("email", user.getEmail());
            customerParams.put("name", user.getName());
            customerParams.put("description", "Customer for user ID: " + user.getId());
            
            // Simular respuesta de Stripe
            String customerId = "cus_" + System.currentTimeMillis();
            
            System.out.println("Created Stripe customer: " + customerId + " for user: " + user.getEmail());
            return customerId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe customer: " + e.getMessage(), e);
        }
    }

    /**
     * Actualizar customer en Stripe
     */
    public void updateStripeCustomer(String customerId, User user) {
        try {
            // En implementación real: Customer.retrieve(customerId).update()
            
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("email", user.getEmail());
            updateParams.put("name", user.getName());
            
            System.out.println("Updated Stripe customer: " + customerId);
            
        } catch (Exception e) {
            throw new RuntimeException("Error updating Stripe customer: " + e.getMessage(), e);
        }
    }

    /**
     * Crear Payment Intent
     */
    public String createPaymentIntent(BigDecimal amount, String currency, String customerId, 
                                    Map<String, String> metadata) {
        try {
            // En implementación real: PaymentIntent.create()
            
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount.multiply(new BigDecimal("100")).longValue()); // Stripe usa centavos
            params.put("currency", currency);
            params.put("customer", customerId);
            params.put("metadata", metadata);
            params.put("automatic_payment_methods", Map.of("enabled", true));
            
            // Simular respuesta
            String paymentIntentId = "pi_" + System.currentTimeMillis();
            String clientSecret = paymentIntentId + "_secret_" + System.currentTimeMillis();
            
            System.out.println("Created PaymentIntent: " + paymentIntentId);
            return clientSecret;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Confirmar Payment Intent
     */
    public boolean confirmPaymentIntent(String paymentIntentId) {
        try {
            // En implementación real: PaymentIntent.retrieve(paymentIntentId).confirm()
            
            if (paymentIntentId == null || paymentIntentId.isEmpty()) {
                return false;
            }
            
            // Simular confirmación exitosa (en real validar con Stripe)
            System.out.println("Confirmed PaymentIntent: " + paymentIntentId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error confirming payment intent: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crear Subscription en Stripe
     */
    public String createStripeSubscription(String customerId, String priceId, 
                                         Map<String, String> metadata) {
        try {
            // En implementación real: Subscription.create()
            
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("items", new Object[]{
                Map.of("price", priceId)
            });
            params.put("metadata", metadata);
            params.put("payment_behavior", "default_incomplete");
            params.put("payment_settings", Map.of("save_default_payment_method", "on_subscription"));
            params.put("expand", new String[]{"latest_invoice.payment_intent"});
            
            // Simular respuesta
            String subscriptionId = "sub_" + System.currentTimeMillis();
            
            System.out.println("Created Stripe subscription: " + subscriptionId);
            return subscriptionId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Actualizar Subscription en Stripe
     */
    public void updateStripeSubscription(String subscriptionId, String newPriceId) {
        try {
            // En implementación real: Subscription.retrieve(subscriptionId).update()
            
            Map<String, Object> params = new HashMap<>();
            params.put("items", new Object[]{
                Map.of("price", newPriceId)
            });
            params.put("proration_behavior", "always_invoice");
            
            System.out.println("Updated Stripe subscription: " + subscriptionId);
            
        } catch (Exception e) {
            throw new RuntimeException("Error updating Stripe subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Cancelar Subscription en Stripe
     */
    public void cancelStripeSubscription(String subscriptionId, boolean immediately) {
        try {
            // En implementación real: Subscription.retrieve(subscriptionId).cancel()
            
            if (immediately) {
                // Cancelar inmediatamente
                Map<String, Object> params = new HashMap<>();
                params.put("invoice_now", true);
                params.put("prorate", true);
            } else {
                // Cancelar al final del período
                Map<String, Object> params = new HashMap<>();
                params.put("cancel_at_period_end", true);
            }
            
            System.out.println("Cancelled Stripe subscription: " + subscriptionId);
            
        } catch (Exception e) {
            throw new RuntimeException("Error cancelling Stripe subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Crear Refund
     */
    public String createRefund(String paymentIntentId, BigDecimal amount, String reason) {
        try {
            // En implementación real: Refund.create()
            
            Map<String, Object> params = new HashMap<>();
            params.put("payment_intent", paymentIntentId);
            params.put("amount", amount.multiply(new BigDecimal("100")).longValue());
            params.put("reason", reason);
            
            // Simular respuesta
            String refundId = "re_" + System.currentTimeMillis();
            
            System.out.println("Created refund: " + refundId + " for payment: " + paymentIntentId);
            return refundId;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating refund: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener Subscription de Stripe
     */
    public Map<String, Object> getStripeSubscription(String subscriptionId) {
        try {
            // En implementación real: Subscription.retrieve(subscriptionId)
            
            Map<String, Object> subscription = new HashMap<>();
            subscription.put("id", subscriptionId);
            subscription.put("status", "active");
            subscription.put("current_period_start", System.currentTimeMillis() / 1000);
            subscription.put("current_period_end", (System.currentTimeMillis() / 1000) + (30 * 24 * 60 * 60));
            
            return subscription;
            
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Stripe subscription: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener Customer de Stripe
     */
    public Map<String, Object> getStripeCustomer(String customerId) {
        try {
            // En implementación real: Customer.retrieve(customerId)
            
            Map<String, Object> customer = new HashMap<>();
            customer.put("id", customerId);
            customer.put("email", "user@example.com");
            customer.put("created", System.currentTimeMillis() / 1000);
            
            return customer;
            
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Stripe customer: " + e.getMessage(), e);
        }
    }

    /**
     * Crear Setup Intent para guardar método de pago
     */
    public String createSetupIntent(String customerId) {
        try {
            // En implementación real: SetupIntent.create()
            
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("payment_method_types", new String[]{"card"});
            params.put("usage", "off_session");
            
            String setupIntentId = "seti_" + System.currentTimeMillis();
            String clientSecret = setupIntentId + "_secret_" + System.currentTimeMillis();
            
            System.out.println("Created SetupIntent: " + setupIntentId);
            return clientSecret;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating setup intent: " + e.getMessage(), e);
        }
    }

    /**
     * Listar métodos de pago del customer
     */
    public Object[] listPaymentMethods(String customerId) {
        try {
            // En implementación real: PaymentMethod.list()
            
            // Simular lista de métodos de pago
            return new Object[]{
                Map.of(
                    "id", "pm_" + System.currentTimeMillis(),
                    "type", "card",
                    "card", Map.of(
                        "brand", "visa",
                        "last4", "4242",
                        "exp_month", 12,
                        "exp_year", 2025
                    )
                )
            };
            
        } catch (Exception e) {
            throw new RuntimeException("Error listing payment methods: " + e.getMessage(), e);
        }
    }

    /**
     * Procesar webhook de Stripe
     */
    public boolean processWebhook(String payload, String signature) {
        try {
            // En implementación real: Webhook.constructEvent()
            
            if (signature == null || !signature.equals(webhookSecret)) {
                System.err.println("Invalid webhook signature");
                return false;
            }
            
            // Simular procesamiento de webhook
            System.out.println("Processing Stripe webhook: " + payload.substring(0, Math.min(100, payload.length())));
            return true;
            
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sincronizar subscription con Stripe
     */
    public void syncSubscriptionWithStripe(Subscription subscription) {
        try {
            if (subscription.getStripeSubscriptionId() != null) {
                Map<String, Object> stripeSubscription = getStripeSubscription(subscription.getStripeSubscriptionId());
                
                // Actualizar estado local basado en Stripe
                String stripeStatus = (String) stripeSubscription.get("status");
                updateLocalSubscriptionStatus(subscription, stripeStatus);
            }
            
        } catch (Exception e) {
            System.err.println("Error syncing subscription with Stripe: " + e.getMessage());
        }
    }

    /**
     * Obtener precio de plan en Stripe
     */
    public String getPriceIdForPlan(Subscription.SubscriptionPlan plan, Subscription.BillingCycle cycle) {
        // En implementación real, estos serían los IDs reales de Stripe
        String planPrefix = plan.name().toLowerCase();
        String cyclePrefix = cycle.name().toLowerCase();
        
        return "price_" + planPrefix + "_" + cyclePrefix;
    }

    /**
     * Verificar si Stripe está configurado correctamente
     */
    public boolean isStripeConfigured() {
        return stripeSecretKey != null && !stripeSecretKey.equals("sk_test_default") &&
               stripePublishableKey != null && !stripePublishableKey.equals("pk_test_default");
    }

    /**
     * Obtener publishable key para frontend
     */
    public String getPublishableKey() {
        return stripePublishableKey;
    }

    /**
     * Actualizar estado de subscription local basado en Stripe
     */
    private void updateLocalSubscriptionStatus(Subscription subscription, String stripeStatus) {
        switch (stripeStatus.toLowerCase()) {
            case "active":
                subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
                break;
            case "past_due":
                subscription.setStatus(Subscription.SubscriptionStatus.PAST_DUE);
                break;
            case "canceled":
                subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
                subscription.setCancelledAt(LocalDateTime.now());
                break;
            case "unpaid":
                subscription.setStatus(Subscription.SubscriptionStatus.SUSPENDED);
                break;
            case "incomplete":
            case "incomplete_expired":
                subscription.setStatus(Subscription.SubscriptionStatus.PENDING);
                break;
            default:
                System.out.println("Unknown Stripe status: " + stripeStatus);
                break;
        }
    }
}
