package com.impulse.billing.controller;

import com.impulse.billing.service.SubscriptionService;
import com.impulse.billing.service.PaymentService;
import com.impulse.billing.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de billing y subscriptions
 * Endpoints para subscriptions, pagos, invoices y Stripe integration
 */
@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private StripeService stripeService;

    // =============== SUBSCRIPTION ENDPOINTS ===============

    /**
     * Crear nueva subscription
     */
    @PostMapping("/subscriptions")
    public ResponseEntity<Map<String, Object>> createSubscription(@RequestBody Map<String, Object> request) {
        try {
            // Mockear User - en implementación real obtener del JWT
            MockUser user = new MockUser();
            user.setId(1L);
            user.setEmail("user@example.com");
            user.setName("Test User");

            String planName = (String) request.get("plan");
            String cycleName = (String) request.get("billingCycle");
            
            MockSubscription.SubscriptionPlan plan = MockSubscription.SubscriptionPlan.valueOf(planName.toUpperCase());
            MockSubscription.BillingCycle cycle = MockSubscription.BillingCycle.valueOf(cycleName.toUpperCase());

            MockSubscription subscription = subscriptionService.createSubscription(user, plan, cycle);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription", convertSubscriptionToMap(subscription));
            response.put("message", "Subscription creada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Actualizar plan de subscription
     */
    @PutMapping("/subscriptions/{id}/plan")
    public ResponseEntity<Map<String, Object>> updateSubscriptionPlan(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String newPlanName = (String) request.get("plan");
            MockSubscription.SubscriptionPlan newPlan = MockSubscription.SubscriptionPlan.valueOf(newPlanName.toUpperCase());

            MockSubscription subscription = subscriptionService.updateSubscriptionPlan(id, newPlan);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription", convertSubscriptionToMap(subscription));
            response.put("message", "Plan actualizado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Cancelar subscription
     */
    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<Map<String, Object>> cancelSubscription(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean immediately) {
        try {
            MockSubscription subscription = subscriptionService.cancelSubscription(id, immediately);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription", convertSubscriptionToMap(subscription));
            response.put("message", "Subscription cancelada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtener subscription del usuario
     */
    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSubscription(@PathVariable Long userId) {
        try {
            // Mockear User
            MockUser user = new MockUser();
            user.setId(userId);

            MockSubscription subscription = subscriptionService.getActiveSubscription(user);

            Map<String, Object> response = new HashMap<>();
            if (subscription != null) {
                response.put("success", true);
                response.put("subscription", convertSubscriptionToMap(subscription));
            } else {
                response.put("success", true);
                response.put("subscription", null);
                response.put("message", "No hay subscription activa");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =============== PAYMENT ENDPOINTS ===============

    /**
     * Crear Payment Intent
     */
    @PostMapping("/payments/intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.getOrDefault("currency", "eur");
            Long userId = Long.valueOf(request.get("userId").toString());

            // Crear customer en Stripe si no existe
            MockUser user = new MockUser();
            user.setId(userId);
            user.setEmail("user@example.com");

            String customerId = stripeService.createStripeCustomer(user);
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("userId", userId.toString());
            
            String clientSecret = stripeService.createPaymentIntent(amount, currency, customerId, metadata);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("clientSecret", clientSecret);
            response.put("publishableKey", stripeService.getPublishableKey());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Confirmar pago
     */
    @PostMapping("/payments/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentIntentId = (String) request.get("paymentIntentId");
            Long userId = Long.valueOf(request.get("userId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            MockUser user = new MockUser();
            user.setId(userId);

            MockPayment.PaymentMethod method = MockPayment.PaymentMethod.STRIPE;
            MockPayment payment = paymentService.processPayment(user, amount, method, paymentIntentId, null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", convertPaymentToMap(payment));
            response.put("message", "Pago procesado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtener historial de pagos del usuario
     */
    @GetMapping("/payments/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPayments(
            @PathVariable Long userId,
            Pageable pageable) {
        try {
            MockUser user = new MockUser();
            user.setId(userId);

            // Simular página de pagos (en implementación real usar paymentService.getUserPaymentHistory)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payments", List.of()); // Lista vacía para mock
            response.put("totalElements", 0);
            response.put("totalPages", 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =============== INVOICE ENDPOINTS ===============

    /**
     * Obtener invoices del usuario
     */
    @GetMapping("/invoices/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInvoices(
            @PathVariable Long userId,
            Pageable pageable) {
        try {
            MockUser user = new MockUser();
            user.setId(userId);

            // Simular página de invoices
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("invoices", List.of());
            response.put("totalElements", 0);
            response.put("totalPages", 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Descargar PDF de invoice
     */
    @GetMapping("/invoices/{invoiceId}/pdf")
    public ResponseEntity<Map<String, Object>> downloadInvoicePdf(@PathVariable Long invoiceId) {
        try {
            // En implementación real: retornar PDF como bytes
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("downloadUrl", "/api/billing/invoices/" + invoiceId + "/download");
            response.put("message", "PDF disponible para descarga");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =============== WEBHOOK ENDPOINTS ===============

    /**
     * Webhook para eventos de Stripe
     */
    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            boolean processed = stripeService.processWebhook(payload, signature);
            
            if (processed) {
                return ResponseEntity.ok("Webhook processed successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid webhook");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing webhook: " + e.getMessage());
        }
    }

    // =============== ANALYTICS ENDPOINTS ===============

    /**
     * Obtener analytics de revenue
     */
    @GetMapping("/analytics/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

            BigDecimal totalRevenue = paymentService.getTotalRevenue(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRevenue", totalRevenue);
            response.put("period", Map.of("start", start, "end", end));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =============== HELPER METHODS ===============

    /**
     * Convertir Subscription a Map para respuesta JSON
     */
    private Map<String, Object> convertSubscriptionToMap(MockSubscription subscription) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", subscription.getId());
        map.put("plan", subscription.getPlan().name());
        map.put("status", subscription.getStatus().name());
        map.put("billingCycle", subscription.getBillingCycle().name());
        map.put("createdAt", subscription.getCreatedAt());
        map.put("currentPeriodStart", subscription.getCurrentPeriodStart());
        map.put("currentPeriodEnd", subscription.getCurrentPeriodEnd());
        map.put("cancelledAt", subscription.getCancelledAt());
        return map;
    }

    /**
     * Convertir Payment a Map para respuesta JSON
     */
    private Map<String, Object> convertPaymentToMap(MockPayment payment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", payment.getId());
        map.put("amount", payment.getAmount());
        map.put("status", payment.getStatus().name());
        map.put("paymentMethod", payment.getPaymentMethod().name());
        map.put("createdAt", payment.getCreatedAt());
        map.put("processedAt", payment.getProcessedAt());
        return map;
    }

    // =============== MOCK CLASSES ===============

    /**
     * Mock User class para compilación
     */
    public static class MockUser {
        private Long id;
        private String email;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /**
     * Mock Subscription class para compilación
     */
    public static class MockSubscription {
        private Long id;
        private SubscriptionPlan plan;
        private SubscriptionStatus status;
        private BillingCycle billingCycle;
        private LocalDateTime createdAt;
        private LocalDateTime currentPeriodStart;
        private LocalDateTime currentPeriodEnd;
        private LocalDateTime cancelledAt;

        public enum SubscriptionPlan { FREE, PREMIUM, PRO, ENTERPRISE }
        public enum SubscriptionStatus { ACTIVE, CANCELLED, SUSPENDED, PAST_DUE, PENDING, TRIALING, INCOMPLETE, INCOMPLETE_EXPIRED }
        public enum BillingCycle { MONTHLY, QUARTERLY, YEARLY }

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public SubscriptionPlan getPlan() { return plan; }
        public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
        public SubscriptionStatus getStatus() { return status; }
        public void setStatus(SubscriptionStatus status) { this.status = status; }
        public BillingCycle getBillingCycle() { return billingCycle; }
        public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getCurrentPeriodStart() { return currentPeriodStart; }
        public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) { this.currentPeriodStart = currentPeriodStart; }
        public LocalDateTime getCurrentPeriodEnd() { return currentPeriodEnd; }
        public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) { this.currentPeriodEnd = currentPeriodEnd; }
        public LocalDateTime getCancelledAt() { return cancelledAt; }
        public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    }

    /**
     * Mock Payment class para compilación
     */
    public static class MockPayment {
        private Long id;
        private BigDecimal amount;
        private PaymentStatus status;
        private PaymentMethod paymentMethod;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public enum PaymentStatus { PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED }
        public enum PaymentMethod { STRIPE, PAYPAL, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, APPLE_PAY, GOOGLE_PAY, SEPA, IDEAL, SOFORT, BANCONTACT }

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    }
}
