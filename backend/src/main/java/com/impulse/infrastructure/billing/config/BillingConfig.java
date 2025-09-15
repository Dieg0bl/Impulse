package com.impulse.infrastructure.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/**
 * Configuración del sistema de facturación y pagos
 * Incluye configuración de Stripe y planes de suscripción
 */
@Configuration
public class BillingConfig {

    // Constantes para evitar duplicación de literales
    private static final String MONTHLY_INTERVAL = "month";

    @Value("${stripe.secret.key:sk_test_dummy}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key:pk_test_dummy}")
    private String stripePublishableKey;

    @Value("${billing.currency:EUR}")
    private String defaultCurrency;

    @Value("${billing.success.url:http://localhost:3000/payment/success}")
    private String successUrl;

    @Value("${billing.cancel.url:http://localhost:3000/payment/cancel}")
    private String cancelUrl;

    /**
     * Inicialización de Stripe
     */
    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Bean para configuración de planes de suscripción
     */
    @Bean
    public BillingPlansConfig billingPlansConfig() {
        return new BillingPlansConfig();
    }

    /**
     * Bean para servicio de facturación
     */
    @Bean
    public BillingService billingService() {
        return new BillingService(this);
    }

    /**
     * Configuración de planes disponibles
     */
    public static class BillingPlansConfig {
        private final Map<String, PlanDetails> plans = new HashMap<>();

        public BillingPlansConfig() {
            // Plan Básico
            plans.put("basic", new PlanDetails(
                "basic",
                "Plan Básico",
                new BigDecimal("9.99"),
                    MONTHLY_INTERVAL,
                "Acceso básico a la plataforma"
            ));

            // Plan Premium
            plans.put("premium", new PlanDetails(
                "premium",
                "Plan Premium",
                new BigDecimal("19.99"),
                    MONTHLY_INTERVAL,
                "Acceso completo con funciones avanzadas"
            ));

            // Plan Coach
            plans.put("coach", new PlanDetails(
                "coach",
                "Plan Coach",
                new BigDecimal("49.99"),
                    MONTHLY_INTERVAL,
                "Plan para coaches con herramientas profesionales"
            ));
        }

        public Map<String, PlanDetails> getPlans() {
            return plans;
        }

        public PlanDetails getPlan(String planId) {
            return plans.get(planId);
        }
    }

    /**
     * Detalles de un plan de suscripción
     */
    public static class PlanDetails {
        private final String id;
        private final String name;
        private final BigDecimal price;
        private final String interval;
        private final String description;

        public PlanDetails(String id, String name, BigDecimal price, String interval, String description) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.interval = interval;
            this.description = description;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public String getInterval() { return interval; }
        public String getDescription() { return description; }
    }

    /**
     * Servicio básico de facturación
     */
    public static class BillingService {
        private final BillingConfig config;

        public BillingService(BillingConfig config) {
            this.config = config;
        }

        /**
         * Crea una sesión de pago de Stripe
         */
        public String createPaymentSession(String planId, String customerId) throws Exception {
            PlanDetails plan = config.billingPlansConfig().getPlan(planId);
            if (plan == null) {
                throw new IllegalArgumentException("Plan no encontrado: " + planId);
            }

            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(config.successUrl)
                .setCancelUrl(config.cancelUrl)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(config.defaultCurrency)
                                .setUnitAmount(plan.getPrice().multiply(new BigDecimal("100")).longValue())
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(plan.getName())
                                        .setDescription(plan.getDescription())
                                        .build()
                                )
                                .setRecurring(
                                    SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                        .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            Session session = Session.create(params);
            return session.getUrl();
        }
    }

    // Getters para acceso a configuración
    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }
}
