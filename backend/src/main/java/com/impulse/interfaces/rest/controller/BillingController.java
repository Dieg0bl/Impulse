package com.impulse.interfaces.rest.controller;

import com.impulse.domain.model.billing.SubscriptionPlan;
import com.impulse.domain.model.billing.UserSubscription;
import com.impulse.infrastructure.billing.StripeService;
import com.impulse.infrastructure.billing.PayPalService;
import com.impulse.interfaces.rest.dto.billing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para billing y suscripciones
 * Solo activo cuando BILLING_ON=true
 */
@RestController
@RequestMapping("/api/billing")
@ConditionalOnProperty(name = "impulse.billing.enabled", havingValue = "true")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private StripeService stripeService;
    
    @Autowired(required = false)
    private PayPalService payPalService;

    // ===== PLANES DE SUSCRIPCIÓN =====

    /**
     * Obtener todos los planes disponibles
     */
    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponseDto>> getAvailablePlans() {
        
        // Mock plans data - en producción vendría de base de datos
        List<PlanResponseDto> plans = List.of(
            createMockPlan("BASIC", "Basic", "0.00", "0.00", false),
            createMockPlan("PRO", "Pro", "12.99", "129.99", true),
            createMockPlan("TEAMS", "Teams", "39.99", "399.99", false)
        );
        
        return ResponseEntity.ok(plans);
    }

    /**
     * Obtener detalles de un plan específico
     */
    @GetMapping("/plans/{planCode}")
    public ResponseEntity<PlanResponseDto> getPlanDetails(@PathVariable String planCode) {
        
        // Mock plan details
        PlanResponseDto plan = switch (planCode.toUpperCase()) {
            case "BASIC" -> createMockPlan("BASIC", "Basic", "0.00", "0.00", false);
            case "PRO" -> createMockPlan("PRO", "Pro", "12.99", "129.99", true);
            case "TEAMS" -> createMockPlan("TEAMS", "Teams", "39.99", "399.99", false);
            default -> null;
        };
        
        if (plan != null) {
            return ResponseEntity.ok(plan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== GESTIÓN DE SUSCRIPCIONES =====

    /**
     * Obtener suscripción actual del usuario
     */
    @GetMapping("/subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDto> getCurrentSubscription(Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        // Mock subscription data
        SubscriptionResponseDto subscription = new SubscriptionResponseDto();
        subscription.setId(1L);
        subscription.setPlanCode("BASIC");
        subscription.setPlanName("Basic");
        subscription.setStatus("ACTIVE");
        subscription.setBillingCycle("MONTHLY");
        subscription.setProvider("STRIPE");
        subscription.setCurrentPrice("0.00");
        subscription.setCurrency("EUR");
        subscription.setIsTrialActive(false);
        subscription.setIsBetaUser(true);
        subscription.setChallengesUsed(5);
        subscription.setMaxChallenges(10);
        subscription.setEvidenceUploadsUsed(15);
        subscription.setMaxEvidenceUploads(50);
        subscription.setStorageUsedMB(250);
        subscription.setMaxStorageGB(5);
        
        return ResponseEntity.ok(subscription);
    }

    /**
     * Crear checkout session para nueva suscripción
     */
    @PostMapping("/subscription/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutResponseDto> createCheckoutSession(
            @Valid @RequestBody CheckoutRequestDto request,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            // Mock checkout session creation
            String sessionUrl;
            
            if ("STRIPE".equals(request.getProvider())) {
                sessionUrl = stripeService.createCheckoutSession(
                    "cus_mock_" + currentUser,
                    createMockSubscriptionPlan(request.getPlanCode()),
                    UserSubscription.BillingCycle.valueOf(request.getBillingCycle()),
                    request.getSuccessUrl(),
                    request.getCancelUrl()
                );
            } else if ("PAYPAL".equals(request.getProvider()) && payPalService != null) {
                sessionUrl = payPalService.createSubscription(
                    request.getPlanCode(),
                    UserSubscription.BillingCycle.valueOf(request.getBillingCycle()),
                    request.getSuccessUrl(),
                    request.getCancelUrl()
                );
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            CheckoutResponseDto response = new CheckoutResponseDto();
            response.setCheckoutUrl(sessionUrl);
            response.setSessionId(extractSessionId(sessionUrl));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualizar suscripción existente
     */
    @PutMapping("/subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDto> updateSubscription(
            @Valid @RequestBody UpdateSubscriptionRequestDto request,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            // Mock subscription update
            SubscriptionResponseDto updatedSubscription = new SubscriptionResponseDto();
            updatedSubscription.setId(1L);
            updatedSubscription.setPlanCode(request.getNewPlanCode());
            updatedSubscription.setBillingCycle(request.getNewBillingCycle());
            updatedSubscription.setStatus("ACTIVE");
            // ... otros campos
            
            return ResponseEntity.ok(updatedSubscription);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancelar suscripción
     */
    @PostMapping("/subscription/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDto> cancelSubscription(
            @RequestParam(defaultValue = "false") boolean immediately,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            // Mock cancellation
            SubscriptionResponseDto canceledSubscription = new SubscriptionResponseDto();
            canceledSubscription.setId(1L);
            canceledSubscription.setStatus(immediately ? "CANCELED" : "ACTIVE");
            canceledSubscription.setCancelAtPeriodEnd(!immediately);
            
            return ResponseEntity.ok(canceledSubscription);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reactivar suscripción cancelada
     */
    @PostMapping("/subscription/reactivate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDto> reactivateSubscription(Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            // Mock reactivation
            SubscriptionResponseDto reactivatedSubscription = new SubscriptionResponseDto();
            reactivatedSubscription.setId(1L);
            reactivatedSubscription.setStatus("ACTIVE");
            reactivatedSubscription.setCancelAtPeriodEnd(false);
            
            return ResponseEntity.ok(reactivatedSubscription);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== PORTAL DEL CLIENTE =====

    /**
     * Crear sesión del portal de facturación
     */
    @PostMapping("/portal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> createPortalSession(
            @RequestParam String returnUrl,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        try {
            String portalUrl = stripeService.createCustomerPortalSession(
                "cus_mock_" + currentUser,
                returnUrl
            );
            
            return ResponseEntity.ok(Map.of("portalUrl", portalUrl));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== WEBHOOKS =====

    /**
     * Webhook de Stripe
     */
    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        try {
            Map<String, Object> event = stripeService.validateWebhook(payload, sigHeader);
            
            // Procesar evento
            String eventType = (String) event.get("type");
            
            switch (eventType) {
                case "invoice.payment_succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "invoice.payment_failed":
                    handlePaymentFailed(event);
                    break;
                case "customer.subscription.created":
                    handleSubscriptionCreated(event);
                    break;
                case "customer.subscription.updated":
                    handleSubscriptionUpdated(event);
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionDeleted(event);
                    break;
                default:
                    // Evento no manejado
                    break;
            }
            
            return ResponseEntity.ok("Webhook processed");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    /**
     * Webhook de PayPal
     */
    @PostMapping("/webhooks/paypal")
    public ResponseEntity<String> handlePayPalWebhook(@RequestBody String payload) {
        
        try {
            if (payPalService != null) {
                payPalService.handleWebhook(payload);
            }
            return ResponseEntity.ok("Webhook processed");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    // ===== MÉTODOS DE UTILIDAD =====

    private PlanResponseDto createMockPlan(String code, String name, String monthlyPrice, String yearlyPrice, boolean isPopular) {
        PlanResponseDto plan = new PlanResponseDto();
        plan.setPlanCode(code);
        plan.setName(name);
        plan.setMonthlyPrice(monthlyPrice);
        plan.setYearlyPrice(yearlyPrice);
        plan.setIsPopular(isPopular);
        plan.setIsActive(true);
        
        // Set features based on plan
        switch (code) {
            case "BASIC":
                plan.setMaxChallenges(10);
                plan.setMaxEvidenceUploads(50);
                plan.setMaxStorageGB(5);
                plan.setHasCoachAccess(false);
                plan.setHasAdvancedAnalytics(false);
                plan.setFeatures(List.of("10 Challenges", "50 Evidence uploads", "5GB Storage"));
                break;
            case "PRO":
                plan.setMaxChallenges(100);
                plan.setMaxEvidenceUploads(500);
                plan.setMaxStorageGB(50);
                plan.setHasCoachAccess(true);
                plan.setHasAdvancedAnalytics(true);
                plan.setFeatures(List.of("100 Challenges", "500 Evidence uploads", "50GB Storage", "Coach Access", "Advanced Analytics"));
                break;
            case "TEAMS":
                plan.setMaxChallenges(1000);
                plan.setMaxEvidenceUploads(5000);
                plan.setMaxStorageGB(500);
                plan.setHasCoachAccess(true);
                plan.setHasAdvancedAnalytics(true);
                plan.setHasApiAccess(true);
                plan.setHasPrioritySupport(true);
                plan.setFeatures(List.of("Unlimited Challenges", "Unlimited Evidence", "500GB Storage", "Team Management", "API Access", "Priority Support"));
                break;
        }
        
        return plan;
    }
    
    private SubscriptionPlan createMockSubscriptionPlan(String planCode) {
        // Mock implementation
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setPlanCode(planCode);
        return plan;
    }
    
    private String extractSessionId(String sessionUrl) {
        // Extract session ID from URL
        return sessionUrl.substring(sessionUrl.lastIndexOf('/') + 1);
    }
    
    // Event handlers
    private void handlePaymentSucceeded(Map<String, Object> event) {
        // Actualizar estado de suscripción
    }
    
    private void handlePaymentFailed(Map<String, Object> event) {
        // Iniciar proceso de dunning
    }
    
    private void handleSubscriptionCreated(Map<String, Object> event) {
        // Crear registro de suscripción en BD
    }
    
    private void handleSubscriptionUpdated(Map<String, Object> event) {
        // Actualizar suscripción en BD
    }
    
    private void handleSubscriptionDeleted(Map<String, Object> event) {
        // Marcar suscripción como cancelada
    }
}
