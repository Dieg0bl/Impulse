package com.impulse.lean.domain;

import com.impulse.lean.domain.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "trial_start_date")
    private LocalDateTime trialStartDate;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Invoice> invoices;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum SubscriptionPlan {
        FREE("Free Plan", BigDecimal.ZERO),
        PREMIUM("Premium Plan", new BigDecimal("9.99")),
        PRO("Pro Plan", new BigDecimal("19.99")),
        ENTERPRISE("Enterprise Plan", new BigDecimal("49.99"));

        private final String displayName;
        private final BigDecimal monthlyPrice;

        SubscriptionPlan(String displayName, BigDecimal monthlyPrice) {
            this.displayName = displayName;
            this.monthlyPrice = monthlyPrice;
        }

        public String getDisplayName() {
            return displayName;
        }

        public BigDecimal getMonthlyPrice() {
            return monthlyPrice;
        }
    }

    public enum SubscriptionStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        CANCELLED,
        SUSPENDED,
        EXPIRED,
        TRIAL,
        PAST_DUE
    }

    public enum BillingCycle {
        MONTHLY,
        YEARLY,
        LIFETIME
    }

    // Utility methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public boolean isTrial() {
        return status == SubscriptionStatus.TRIAL;
    }

    public boolean isExpired() {
        return status == SubscriptionStatus.EXPIRED || 
               (endDate != null && endDate.isBefore(LocalDateTime.now()));
    }

    public boolean hasTrial() {
        return trialStartDate != null && trialEndDate != null;
    }

    public long getDaysUntilExpiry() {
        if (endDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }

    public BigDecimal getEffectiveAmount() {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = amount.multiply(discountPercentage).divide(new BigDecimal("100"));
            return amount.subtract(discount);
        }
        return amount;
    }
}
