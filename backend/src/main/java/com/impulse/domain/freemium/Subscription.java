package com.impulse.domain.freemium;package com.impulse.domain.freemium;package com.impulse.domain.freemium;



import com.impulse.domain.user.UserId;

import lombok.Builder;

import lombok.Getter;import com.impulse.domain.user.UserId;import com.impulse.domain.user.UserId;



import java.time.LocalDateTime;import lombok.Builder;import lombok.Builder;

import java.util.UUID;

import lombok.Getter;import lombok.Getter;

@Getter

@Builder

public class Subscription {

    private final UUID id;import java.time.LocalDateTime;import java.time.LocalDateTime;

    private final UserId userId;

    private final String planId;import java.util.UUID;import java.util.UUID;

    private final String planName;

    private final String status;

    private final String stripeSubscriptionId;

    private final String stripeCustomerId;@Getter@Getter

    private final LocalDateTime currentPeriodStart;

    private final LocalDateTime currentPeriodEnd;@Builder@Builder

    private final LocalDateTime trialStart;

    private final LocalDateTime trialEnd;public class Subscription {public class Subscription {

    private final Boolean cancelAtPeriodEnd;

    private final LocalDateTime cancelledAt;    private final UUID id;    private final UUID id;

    private final LocalDateTime endedAt;

    private final String billingCycle;    private final UserId userId;    private final UserId userId;

    private final Double amount;

    private final String currency;    private final String planId;    private final String planId;

    private final Double discountAmount;

    private final Double taxAmount;    private final String planName;    private final String planName;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;    private final String status;    private final String status;



    public static Subscription create(UserId userId, String planId, String planName, String stripeSubscriptionId,    private final String stripeSubscriptionId;    private final String stripeSubscriptionId;

                                    String stripeCustomerId, String billingCycle, Double amount, String currency) {

        LocalDateTime now = LocalDateTime.now();    private final String stripeCustomerId;    private final String stripeCustomerId;

        return Subscription.builder()

                .id(UUID.randomUUID())    private final LocalDateTime currentPeriodStart;    private final LocalDateTime currentPeriodStart;

                .userId(userId)

                .planId(planId)    private final LocalDateTime currentPeriodEnd;    private final LocalDateTime currentPeriodEnd;

                .planName(planName)

                .status("active")    private final LocalDateTime trialStart;    private final LocalDateTime trialStart;

                .stripeSubscriptionId(stripeSubscriptionId)

                .stripeCustomerId(stripeCustomerId)    private final LocalDateTime trialEnd;    private final LocalDateTime trialEnd;

                .billingCycle(billingCycle)

                .amount(amount)    private final Boolean cancelAtPeriodEnd;    private final Boolean cancelAtPeriodEnd;

                .currency(currency)

                .cancelAtPeriodEnd(false)    private final LocalDateTime cancelledAt;    private final LocalDateTime cancelledAt;

                .createdAt(now)

                .updatedAt(now)    private final LocalDateTime endedAt;    private final LocalDateTime endedAt;

                .build();

    }    private final String billingCycle;    private final String billingCycle;

}
    private final Double amount;    private final Double amount;

    private final String currency;    private final String currency;

    private final Double discountAmount;    private final Double discountAmount;

    private final Double taxAmount;    private final Double taxAmount;

    private final LocalDateTime createdAt;    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;    private final LocalDateTime updatedAt;



    public static Subscription create(UserId userId, String planId, String planName, String stripeSubscriptionId,    public static Subscription create(UserId userId, String planId, String planName, String stripeSubscriptionId,

                                    String stripeCustomerId, String billingCycle, Double amount, String currency) {                                    String stripeCustomerId, String billingCycle, Double amount, String currency) {

        LocalDateTime now = LocalDateTime.now();        LocalDateTime now = LocalDateTime.now();

        return Subscription.builder()        return Subscription.builder()

                .id(UUID.randomUUID())                .id(UUID.randomUUID())

                .userId(userId)                .userId(userId)

                .planId(planId)                .planId(planId)

                .planName(planName)                .planName(planName)

                .status("active")                .status("active")

                .stripeSubscriptionId(stripeSubscriptionId)                .stripeSubscriptionId(stripeSubscriptionId)

                .stripeCustomerId(stripeCustomerId)                .stripeCustomerId(stripeCustomerId)

                .billingCycle(billingCycle)                .billingCycle(billingCycle)

                .amount(amount)                .amount(amount)

                .currency(currency)                .currency(currency)

                .cancelAtPeriodEnd(false)                .cancelAtPeriodEnd(false)

                .createdAt(now)                .createdAt(now)

                .updatedAt(now)                .updatedAt(now)

                .build();                .build();

    }    }

}}
    private LocalDateTime updatedAt;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStripeSubscriptionId() { return stripeSubscriptionId; }
    public void setStripeSubscriptionId(String stripeSubscriptionId) { this.stripeSubscriptionId = stripeSubscriptionId; }
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }
    public LocalDateTime getCurrentPeriodStart() { return currentPeriodStart; }
    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) { this.currentPeriodStart = currentPeriodStart; }
    public LocalDateTime getCurrentPeriodEnd() { return currentPeriodEnd; }
    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) { this.currentPeriodEnd = currentPeriodEnd; }
    public LocalDateTime getTrialStart() { return trialStart; }
    public void setTrialStart(LocalDateTime trialStart) { this.trialStart = trialStart; }
    public LocalDateTime getTrialEnd() { return trialEnd; }
    public void setTrialEnd(LocalDateTime trialEnd) { this.trialEnd = trialEnd; }
    public Boolean getCancelAtPeriodEnd() { return cancelAtPeriodEnd; }
    public void setCancelAtPeriodEnd(Boolean cancelAtPeriodEnd) { this.cancelAtPeriodEnd = cancelAtPeriodEnd; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
