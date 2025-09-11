package com.impulse.lean.service;

import com.impulse.lean.domain.Payment;
import com.impulse.lean.domain.User;
import com.impulse.lean.domain.Subscription;
import com.impulse.lean.repository.PaymentRepository;
import com.impulse.lean.repository.SubscriptionRepository;
import com.impulse.lean.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de pagos y transacciones
 * Maneja procesamiento de pagos, reembolsos, y analytics de revenue
 */
@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Procesa un nuevo pago
     */
    public Payment processPayment(User user, BigDecimal amount, Payment.PaymentMethod paymentMethod, 
                                String stripePaymentIntentId, Long subscriptionId) {
        try {
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(amount);
            payment.setPaymentMethod(paymentMethod);
            payment.setStripePaymentId(stripePaymentIntentId);
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
            payment.setCreatedAt(LocalDateTime.now());
            
            if (subscriptionId != null) {
                Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);
                subscription.ifPresent(payment::setSubscription);
            }

            // Confirmar pago con Stripe
            boolean stripeSuccess = stripeService.confirmPaymentIntent(stripePaymentIntentId);
            
            if (stripeSuccess) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setProcessedAt(LocalDateTime.now());
                
                // Actualizar subscription si existe
                if (payment.getSubscription() != null) {
                    updateSubscriptionAfterPayment(payment.getSubscription(), payment);
                }
                
                // Enviar notificación de éxito
                notificationService.sendPaymentSuccessNotification(user, payment);
                
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Stripe payment confirmation failed");
                
                // Enviar notificación de fallo
                notificationService.sendPaymentFailureNotification(user, payment);
            }

            return paymentRepository.save(payment);
            
        } catch (Exception e) {
            // Crear payment record con estado fallido
            Payment failedPayment = new Payment();
            failedPayment.setUser(user);
            failedPayment.setAmount(amount);
            failedPayment.setPaymentMethod(paymentMethod);
            failedPayment.setStatus(Payment.PaymentStatus.FAILED);
            failedPayment.setFailureReason(e.getMessage());
            failedPayment.setCreatedAt(LocalDateTime.now());
            
            return paymentRepository.save(failedPayment);
        }
    }

    /**
     * Procesa un reembolso
     */
    public Payment processRefund(Payment originalPayment, BigDecimal refundAmount, String reason) {
        if (originalPayment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos completados");
        }

        if (refundAmount.compareTo(originalPayment.getAmount()) > 0) {
            throw new IllegalArgumentException("El monto del reembolso no puede ser mayor al pago original");
        }

        try {
            // Procesar reembolso en Stripe
            String stripeRefundId = stripeService.createRefund(
                originalPayment.getStripePaymentId(), 
                refundAmount, 
                reason
            );

            // Crear registro de reembolso
            Payment refund = new Payment();
            refund.setUser(originalPayment.getUser());
            refund.setAmount(refundAmount.negate()); // Monto negativo para reembolsos
            refund.setPaymentMethod(originalPayment.getPaymentMethod());
            refund.setStripePaymentId(stripeRefundId);
            refund.setStatus(Payment.PaymentStatus.REFUNDED);
            refund.setFailureReason(reason);
            refund.setCreatedAt(LocalDateTime.now());
            refund.setProcessedAt(LocalDateTime.now());
            refund.setSubscription(originalPayment.getSubscription());

            Payment savedRefund = paymentRepository.save(refund);

            // Actualizar pago original
            originalPayment.setStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(originalPayment);

            // Actualizar subscription si existe
            if (originalPayment.getSubscription() != null) {
                handleSubscriptionAfterRefund(originalPayment.getSubscription(), savedRefund);
            }

            // Enviar notificación
            notificationService.sendRefundProcessedNotification(originalPayment.getUser(), savedRefund);

            return savedRefund;

        } catch (Exception e) {
            throw new RuntimeException("Error procesando reembolso: " + e.getMessage(), e);
        }
    }

    /**
     * Reintentar pago fallido
     */
    public Payment retryFailedPayment(Long paymentId) {
        Payment failedPayment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (failedPayment.getStatus() != Payment.PaymentStatus.FAILED) {
            throw new IllegalStateException("Solo se pueden reintentar pagos fallidos");
        }

        // Crear nuevo intento de pago
        return processPayment(
            failedPayment.getUser(),
            failedPayment.getAmount(),
            failedPayment.getPaymentMethod(),
            null, // Nuevo payment intent
            failedPayment.getSubscription() != null ? failedPayment.getSubscription().getId() : null
        );
    }

    /**
     * Obtener historial de pagos del usuario
     */
    @Transactional(readOnly = true)
    public Page<Payment> getUserPaymentHistory(User user, Pageable pageable) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Obtener pagos exitosos en rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Payment> getSuccessfulPayments(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findSuccessfulPaymentsByDateRange(startDate, endDate);
    }

    /**
     * Obtener revenue total por rango de fechas
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getTotalRevenueByDateRange(startDate, endDate);
    }

    /**
     * Obtener distribución de métodos de pago
     */
    @Transactional(readOnly = true)
    public Map<Payment.PaymentMethod, Long> getPaymentMethodDistribution() {
        return paymentRepository.getPaymentMethodDistribution();
    }

    /**
     * Obtener pagos fallidos para reintento automático
     */
    @Transactional(readOnly = true)
    public List<Payment> getFailedPaymentsForRetry(int maxRetries) {
        return paymentRepository.findFailedPaymentsForRetry(maxRetries);
    }

    /**
     * Obtener analytics de revenue por subscription plan
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getRevenueBySubscriptionPlan(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getRevenueBySubscriptionPlan(startDate, endDate);
    }

    /**
     * Obtener estadísticas de pagos por día
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDailyPaymentStats(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getDailyPaymentStats(startDate, endDate);
    }

    /**
     * Verificar si usuario tiene pagos pendientes
     */
    @Transactional(readOnly = true)
    public boolean hasPendingPayments(User user) {
        return paymentRepository.countPendingPaymentsByUser(user) > 0;
    }

    /**
     * Obtener último pago exitoso del usuario
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getLastSuccessfulPayment(User user) {
        return paymentRepository.findLastSuccessfulPaymentByUser(user);
    }

    /**
     * Procesar pagos automáticos para subscriptions
     */
    public void processAutomaticPayments() {
        List<Subscription> subscriptionsForBilling = subscriptionRepository
            .findSubscriptionsForBilling(LocalDateTime.now());

        for (Subscription subscription : subscriptionsForBilling) {
            try {
                processSubscriptionPayment(subscription);
            } catch (Exception e) {
                System.err.println("Error procesando pago automático para subscription " + 
                    subscription.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Procesar pago de subscription específica
     */
    private void processSubscriptionPayment(Subscription subscription) {
        BigDecimal amount = getSubscriptionAmount(subscription.getPlan());
        
        Payment payment = processPayment(
            subscription.getUser(),
            amount,
            Payment.PaymentMethod.STRIPE, // Por defecto para pagos automáticos
            null, // Se creará nuevo payment intent
            subscription.getId()
        );

        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
            // Extender período de subscription
            subscription.setCurrentPeriodEnd(
                calculateNextBillingDate(subscription.getBillingCycle(), subscription.getCurrentPeriodEnd())
            );
            subscriptionRepository.save(subscription);
        } else {
            // Marcar subscription como past due
            subscription.setStatus(Subscription.SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(subscription);
            
            // Programar reintento
            schedulePaymentRetry(subscription);
        }
    }

    /**
     * Actualizar subscription después de pago exitoso
     */
    private void updateSubscriptionAfterPayment(Subscription subscription, Payment payment) {
        if (subscription.getStatus() == Subscription.SubscriptionStatus.PAST_DUE) {
            subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        }
        
        // Actualizar última fecha de pago
        subscription.setCurrentPeriodStart(LocalDateTime.now());
        subscription.setCurrentPeriodEnd(
            calculateNextBillingDate(subscription.getBillingCycle(), LocalDateTime.now())
        );
        
        subscriptionRepository.save(subscription);
    }

    /**
     * Manejar subscription después de reembolso
     */
    private void handleSubscriptionAfterRefund(Subscription subscription, Payment refund) {
        // Lógica específica dependiendo de la política de reembolsos
        // Puede cancelar, suspender o ajustar el período de la subscription
        
        if (refund.getAmount().abs().equals(getSubscriptionAmount(subscription.getPlan()))) {
            // Reembolso completo - cancelar subscription
            subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
            subscription.setCancelledAt(LocalDateTime.now());
        } else {
            // Reembolso parcial - ajustar crédito o período
            subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        }
        
        subscriptionRepository.save(subscription);
    }

    /**
     * Obtener monto de subscription según plan
     */
    private BigDecimal getSubscriptionAmount(Subscription.SubscriptionPlan plan) {
        switch (plan) {
            case FREE:
                return BigDecimal.ZERO;
            case PREMIUM:
                return new BigDecimal("9.99");
            case PRO:
                return new BigDecimal("19.99");
            case ENTERPRISE:
                return new BigDecimal("49.99");
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Calcular próxima fecha de billing
     */
    private LocalDateTime calculateNextBillingDate(Subscription.BillingCycle billingCycle, LocalDateTime fromDate) {
        switch (billingCycle) {
            case MONTHLY:
                return fromDate.plusMonths(1);
            case QUARTERLY:
                return fromDate.plusMonths(3);
            case YEARLY:
                return fromDate.plusYears(1);
            default:
                return fromDate.plusMonths(1);
        }
    }

    /**
     * Programar reintento de pago fallido
     */
    private void schedulePaymentRetry(Subscription subscription) {
        // Lógica para programar reintentos automáticos
        // Puede usar scheduled tasks o queue system
        System.out.println("Programando reintento de pago para subscription: " + subscription.getId());
    }
}
