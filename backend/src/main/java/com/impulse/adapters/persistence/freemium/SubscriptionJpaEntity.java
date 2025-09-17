package com.impulse.adapters.persistence.freemium;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionJpaEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String userId;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(nullable = false)
    private String status;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(name = "trial_start")
    private LocalDateTime trialStart;

    @Column(name = "trial_end")
    private LocalDateTime trialEnd;

    @Column(name = "cancel_at_period_end")
    private Boolean cancelAtPeriodEnd;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "billing_cycle")
    private String billingCycle;

    @Column(precision = 10, scale = 2)
    private Double amount;

    @Column(length = 3)
    private String currency;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private Double discountAmount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private Double taxAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}