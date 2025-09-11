package com.impulse.billing.model;

import com.impulse.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - Subscription Domain Model
 * 
 * Represents user subscription plans and billing information
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stripe_subscription_id", unique = true)
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private SubscriptionPlan planType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "is_trial", nullable = false)
    private Boolean isTrial = false;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    @Column(length = 500)
    private String metadata;

    @Column(name = "grace_period_ends_at")
    private LocalDateTime gracePeriodEndsAt;

    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    @Column(name = "failed_payments_count")
    private Integer failedPaymentsCount = 0;

    // Enums

    public enum SubscriptionPlan {
        FREE("Free", BigDecimal.ZERO, "Basic features"),
        PREMIUM("Premium", new BigDecimal("9.99"), "Premium features with gamification"),
        PRO("Pro", new BigDecimal("19.99"), "All features plus analytics"),
        ENTERPRISE("Enterprise", new BigDecimal("49.99"), "Enterprise features and support");

        private final String displayName;
        private final BigDecimal defaultPrice;
        private final String description;

        SubscriptionPlan(String displayName, BigDecimal defaultPrice, String description) {
            this.displayName = displayName;
            this.defaultPrice = defaultPrice;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public BigDecimal getDefaultPrice() { return defaultPrice; }
        public String getDescription() { return description; }
    }

    public enum SubscriptionStatus {
        ACTIVE("Active"),
        TRIALING("Trialing"),
        PAST_DUE("Past Due"),
        CANCELED("Canceled"),
        UNPAID("Unpaid"),
        INCOMPLETE("Incomplete"),
        INCOMPLETE_EXPIRED("Incomplete Expired"),
        PAUSED("Paused");

        private final String displayName;

        SubscriptionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public boolean isActive() {
            return this == ACTIVE || this == TRIALING;
        }

        public boolean requiresPayment() {
            return this == PAST_DUE || this == UNPAID || this == INCOMPLETE;
        }
    }

    public enum BillingCycle {
        MONTHLY("Monthly", 1),
        QUARTERLY("Quarterly", 3),
        YEARLY("Yearly", 12);

        private final String displayName;
        private final int months;

        BillingCycle(String displayName, int months) {
            this.displayName = displayName;
            this.months = months;
        }

        public String getDisplayName() { return displayName; }
        public int getMonths() { return months; }
    }

    // Constructors
    public Subscription() {}

    public Subscription(User user, SubscriptionPlan planType, BillingCycle billingCycle) {
        this.user = user;
        this.planType = planType;
        this.billingCycle = billingCycle;
        this.status = SubscriptionStatus.INCOMPLETE;
        this.price = planType.getDefaultPrice();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public SubscriptionPlan getPlanType() {
        return planType;
    }

    public void setPlanType(SubscriptionPlan planType) {
        this.planType = planType;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public LocalDateTime getTrialEndsAt() {
        return trialEndsAt;
    }

    public void setTrialEndsAt(LocalDateTime trialEndsAt) {
        this.trialEndsAt = trialEndsAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public LocalDateTime getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDateTime nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getGracePeriodEndsAt() {
        return gracePeriodEndsAt;
    }

    public void setGracePeriodEndsAt(LocalDateTime gracePeriodEndsAt) {
        this.gracePeriodEndsAt = gracePeriodEndsAt;
    }

    public LocalDateTime getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDateTime lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public Integer getFailedPaymentsCount() {
        return failedPaymentsCount;
    }

    public void setFailedPaymentsCount(Integer failedPaymentsCount) {
        this.failedPaymentsCount = failedPaymentsCount;
    }

    // Business Methods
    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean requiresPayment() {
        return status != null && status.requiresPayment();
    }

    public boolean isTrialActive() {
        return isTrial && trialEndsAt != null && trialEndsAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return endsAt != null && endsAt.isBefore(LocalDateTime.now());
    }

    public boolean isInGracePeriod() {
        return gracePeriodEndsAt != null && gracePeriodEndsAt.isAfter(LocalDateTime.now());
    }

    public void incrementFailedPayments() {
        this.failedPaymentsCount++;
    }

    public void resetFailedPayments() {
        this.failedPaymentsCount = 0;
    }

    public BigDecimal calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.autoRenew = false;
    }

    public void pause() {
        this.status = SubscriptionStatus.PAUSED;
    }

    public void reactivate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.canceledAt = null;
        this.autoRenew = true;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", planType=" + planType +
                ", status=" + status +
                ", price=" + price +
                ", billingCycle=" + billingCycle +
                '}';
    }
}
