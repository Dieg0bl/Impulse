package com.impulse.lean.service;

import com.impulse.lean.domain.model.Subscription;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Subscription Service
 * 
 * Service for subscription management and lifecycle operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Value("${stripe.trial.days:14}")
    private int defaultTrialDays;

    @Value("${stripe.grace.period.days:3}")
    private int gracePeriodDays;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create new subscription for user
     */
    public Subscription createSubscription(User user, Subscription.SubscriptionPlan planType, 
                                         Subscription.BillingCycle billingCycle, boolean startTrial) {
        try {
            // Check if user already has active subscription
            Optional<Subscription> existing = subscriptionRepository.findActiveSubscriptionByUser(user);
            if (existing.isPresent()) {
                throw new IllegalStateException("User already has an active subscription");
            }

            // Create subscription
            Subscription subscription = new Subscription(user, planType, billingCycle);
            
            if (startTrial && planType != Subscription.SubscriptionPlan.FREE) {
                subscription.setIsTrial(true);
                subscription.setStatus(Subscription.SubscriptionStatus.TRIALING);
                subscription.setTrialEndsAt(LocalDateTime.now().plusDays(defaultTrialDays));
            } else if (planType == Subscription.SubscriptionPlan.FREE) {
                subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
                subscription.setStartedAt(LocalDateTime.now());
                subscription.setPrice(BigDecimal.ZERO);
            }

            // Create in Stripe if not free plan
            if (planType != Subscription.SubscriptionPlan.FREE) {
                String stripeCustomerId = stripeService.createOrGetCustomer(user);
                subscription.setStripeCustomerId(stripeCustomerId);
                
                String stripeSubscriptionId = stripeService.createSubscription(
                    stripeCustomerId, planType, billingCycle, startTrial);
                subscription.setStripeSubscriptionId(stripeSubscriptionId);
            }

            // Calculate next billing date
            if (subscription.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                subscription.setNextBillingDate(calculateNextBillingDate(billingCycle));
            } else if (subscription.getIsTrial()) {
                subscription.setNextBillingDate(subscription.getTrialEndsAt());
            }

            Subscription saved = subscriptionRepository.save(subscription);
            
            // Send welcome notification
            notificationService.sendSubscriptionWelcome(user, saved);
            
            logger.info("Created subscription for user {} with plan {}", user.getId(), planType);
            return saved;
            
        } catch (Exception e) {
            logger.error("Error creating subscription for user {} with plan {}: {}", 
                        user.getId(), planType, e.getMessage());
            throw new RuntimeException("Failed to create subscription", e);
        }
    }

    /**
     * Update subscription plan
     */
    public Subscription updateSubscriptionPlan(Long subscriptionId, Subscription.SubscriptionPlan newPlan) {
        try {
            Optional<Subscription> existing = subscriptionRepository.findById(subscriptionId);
            if (existing.isEmpty()) {
                throw new IllegalArgumentException("Subscription not found");
            }

            Subscription subscription = existing.get();
            Subscription.SubscriptionPlan oldPlan = subscription.getPlanType();

            // Update in Stripe if not free plans
            if (newPlan != Subscription.SubscriptionPlan.FREE && 
                subscription.getStripeSubscriptionId() != null) {
                stripeService.updateSubscriptionPlan(subscription.getStripeSubscriptionId(), newPlan);
            }

            // Update subscription
            subscription.setPlanType(newPlan);
            subscription.setPrice(newPlan.getDefaultPrice());
            subscription.setUpdatedAt(LocalDateTime.now());

            Subscription saved = subscriptionRepository.save(subscription);
            
            // Send notification
            notificationService.sendPlanChangeNotification(subscription.getUser(), oldPlan, newPlan);
            
            logger.info("Updated subscription {} from {} to {}", subscriptionId, oldPlan, newPlan);
            return saved;
            
        } catch (Exception e) {
            logger.error("Error updating subscription {}: {}", subscriptionId, e.getMessage());
            throw new RuntimeException("Failed to update subscription", e);
        }
    }

    /**
     * Cancel subscription
     */
    public Subscription cancelSubscription(Long subscriptionId, boolean immediately) {
        try {
            Optional<Subscription> existing = subscriptionRepository.findById(subscriptionId);
            if (existing.isEmpty()) {
                throw new IllegalArgumentException("Subscription not found");
            }

            Subscription subscription = existing.get();

            // Cancel in Stripe
            if (subscription.getStripeSubscriptionId() != null) {
                stripeService.cancelSubscription(subscription.getStripeSubscriptionId(), immediately);
            }

            // Update subscription
            if (immediately) {
                subscription.cancel();
                subscription.setEndsAt(LocalDateTime.now());
            } else {
                subscription.setAutoRenew(false);
                // Set end date to next billing date
                subscription.setEndsAt(subscription.getNextBillingDate());
            }

            Subscription saved = subscriptionRepository.save(subscription);
            
            // Send cancellation notification
            notificationService.sendCancellationNotification(subscription.getUser(), saved, immediately);
            
            logger.info("Canceled subscription {} (immediate: {})", subscriptionId, immediately);
            return saved;
            
        } catch (Exception e) {
            logger.error("Error canceling subscription {}: {}", subscriptionId, e.getMessage());
            throw new RuntimeException("Failed to cancel subscription", e);
        }
    }

    /**
     * Reactivate canceled subscription
     */
    public Subscription reactivateSubscription(Long subscriptionId) {
        try {
            Optional<Subscription> existing = subscriptionRepository.findById(subscriptionId);
            if (existing.isEmpty()) {
                throw new IllegalArgumentException("Subscription not found");
            }

            Subscription subscription = existing.get();
            if (subscription.getStatus() != Subscription.SubscriptionStatus.CANCELED) {
                throw new IllegalStateException("Only canceled subscriptions can be reactivated");
            }

            // Reactivate in Stripe
            if (subscription.getStripeSubscriptionId() != null) {
                stripeService.reactivateSubscription(subscription.getStripeSubscriptionId());
            }

            // Update subscription
            subscription.reactivate();
            subscription.setNextBillingDate(calculateNextBillingDate(subscription.getBillingCycle()));

            Subscription saved = subscriptionRepository.save(subscription);
            
            // Send reactivation notification
            notificationService.sendReactivationNotification(subscription.getUser(), saved);
            
            logger.info("Reactivated subscription {}", subscriptionId);
            return saved;
            
        } catch (Exception e) {
            logger.error("Error reactivating subscription {}: {}", subscriptionId, e.getMessage());
            throw new RuntimeException("Failed to reactivate subscription", e);
        }
    }

    /**
     * Process trial ending
     */
    public void processTrialEnding(Subscription subscription) {
        try {
            if (!subscription.getIsTrial() || subscription.getTrialEndsAt().isAfter(LocalDateTime.now())) {
                return;
            }

            // Attempt to charge customer
            boolean paymentSuccessful = stripeService.processTrialEndPayment(subscription);
            
            if (paymentSuccessful) {
                subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
                subscription.setIsTrial(false);
                subscription.setStartedAt(LocalDateTime.now());
                subscription.setNextBillingDate(calculateNextBillingDate(subscription.getBillingCycle()));
            } else {
                subscription.setStatus(Subscription.SubscriptionStatus.PAST_DUE);
                subscription.setGracePeriodEndsAt(LocalDateTime.now().plusDays(gracePeriodDays));
                subscription.incrementFailedPayments();
            }

            subscriptionRepository.save(subscription);
            
            // Send appropriate notification
            if (paymentSuccessful) {
                notificationService.sendTrialConversionSuccess(subscription.getUser(), subscription);
            } else {
                notificationService.sendPaymentFailed(subscription.getUser(), subscription);
            }
            
            logger.info("Processed trial ending for subscription {}: payment successful = {}", 
                       subscription.getId(), paymentSuccessful);
            
        } catch (Exception e) {
            logger.error("Error processing trial ending for subscription {}: {}", 
                        subscription.getId(), e.getMessage());
        }
    }

    /**
     * Process subscription billing
     */
    public void processSubscriptionBilling(Subscription subscription) {
        try {
            if (!subscription.isActive() || 
                subscription.getNextBillingDate().isAfter(LocalDateTime.now())) {
                return;
            }

            // Process payment
            boolean paymentSuccessful = stripeService.processSubscriptionPayment(subscription);
            
            if (paymentSuccessful) {
                subscription.setLastPaymentDate(LocalDateTime.now());
                subscription.setNextBillingDate(calculateNextBillingDate(subscription.getBillingCycle()));
                subscription.resetFailedPayments();
                
                if (subscription.getStatus() == Subscription.SubscriptionStatus.PAST_DUE) {
                    subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
                    subscription.setGracePeriodEndsAt(null);
                }
            } else {
                subscription.setStatus(Subscription.SubscriptionStatus.PAST_DUE);
                subscription.setGracePeriodEndsAt(LocalDateTime.now().plusDays(gracePeriodDays));
                subscription.incrementFailedPayments();
                
                // Cancel if too many failed payments
                if (subscription.getFailedPaymentsCount() >= 3) {
                    subscription.cancel();
                    notificationService.sendSubscriptionCanceledDueToPaymentFailure(
                        subscription.getUser(), subscription);
                }
            }

            subscriptionRepository.save(subscription);
            
            // Send notification
            if (paymentSuccessful) {
                notificationService.sendPaymentSuccess(subscription.getUser(), subscription);
            } else {
                notificationService.sendPaymentFailed(subscription.getUser(), subscription);
            }
            
            logger.info("Processed billing for subscription {}: payment successful = {}", 
                       subscription.getId(), paymentSuccessful);
            
        } catch (Exception e) {
            logger.error("Error processing billing for subscription {}: {}", 
                        subscription.getId(), e.getMessage());
        }
    }

    /**
     * Get active subscription for user
     */
    public Optional<Subscription> getActiveSubscription(User user) {
        return subscriptionRepository.findActiveSubscriptionByUser(user);
    }

    /**
     * Get subscription by Stripe ID
     */
    public Optional<Subscription> getByStripeSubscriptionId(String stripeSubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);
    }

    /**
     * Get subscriptions due for billing
     */
    public List<Subscription> getSubscriptionsDueForBilling() {
        return subscriptionRepository.findSubscriptionsForBilling(
            Subscription.SubscriptionStatus.ACTIVE, LocalDateTime.now());
    }

    /**
     * Get trials ending soon
     */
    public List<Subscription> getTrialsEndingSoon() {
        return subscriptionRepository.findTrialsEndingSoon(LocalDateTime.now());
    }

    /**
     * Get subscriptions with expired grace periods
     */
    public List<Subscription> getExpiredGracePeriods() {
        return subscriptionRepository.findExpiredGracePeriods(LocalDateTime.now());
    }

    /**
     * Get subscription statistics
     */
    public Object getSubscriptionStatistics() {
        try {
            Long activeCount = subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.ACTIVE);
            Long trialingCount = subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.TRIALING);
            Long canceledCount = subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.CANCELED);
            Long pastDueCount = subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.PAST_DUE);
            
            Double monthlyRevenue = subscriptionRepository.getTotalMonthlyRecurringRevenue();
            Double averageRevenue = subscriptionRepository.getAverageRevenuePerUser();
            
            List<Object[]> planDistribution = subscriptionRepository.getSubscriptionDistribution();
            
            return new Object() {
                public final Long activeSubscriptions = activeCount;
                public final Long trialingSubscriptions = trialingCount;
                public final Long canceledSubscriptions = canceledCount;
                public final Long pastDueSubscriptions = pastDueCount;
                public final Double monthlyRecurringRevenue = monthlyRevenue != null ? monthlyRevenue : 0.0;
                public final Double averageRevenuePerUser = averageRevenue != null ? averageRevenue : 0.0;
                public final List<Object[]> planDistribution = planDistribution;
            };
            
        } catch (Exception e) {
            logger.error("Error getting subscription statistics: {}", e.getMessage());
            throw new RuntimeException("Failed to get subscription statistics", e);
        }
    }

    // Private helper methods

    private LocalDateTime calculateNextBillingDate(Subscription.BillingCycle billingCycle) {
        LocalDateTime now = LocalDateTime.now();
        switch (billingCycle) {
            case MONTHLY:
                return now.plusMonths(1);
            case QUARTERLY:
                return now.plusMonths(3);
            case YEARLY:
                return now.plusYears(1);
            default:
                return now.plusMonths(1);
        }
    }
}
