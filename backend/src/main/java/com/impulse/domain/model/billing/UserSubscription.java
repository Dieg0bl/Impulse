package com.impulse.domain.model.billing;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para suscripciones de usuario
 */
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "user_id")
    private Long userId;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle = BillingCycle.MONTHLY;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider = PaymentProvider.STRIPE;
    
    // IDs externos
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private String paypalSubscriptionId;
    private String paypalPayerId;
    
    // Información de facturación
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal currentPrice;
    
    @Column(length = 3)
    private String currency = "EUR";
    
    // Fechas importantes
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private LocalDateTime nextBillingDate;
    
    private LocalDateTime canceledAt;
    private LocalDateTime pausedAt;
    
    // Beta y trials
    @Column(nullable = false)
    private Boolean isBetaUser = false;
    
    @Column(nullable = false)
    private Boolean isTrialActive = false;
    
    private LocalDateTime trialEndDate;
    
    // Métricas de uso
    @Column(nullable = false)
    private Integer challengesUsed = 0;
    
    @Column(nullable = false)
    private Integer evidenceUploadsUsed = 0;
    
    @Column(nullable = false)
    private Integer storageUsedMB = 0;
    
    // Dunning (gestión de fallos de pago)
    @Column(nullable = false)
    private Integer failedPaymentAttempts = 0;
    
    private LocalDateTime lastFailedPayment;
    private LocalDateTime dunningStartDate;
    
    // Metadatos
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public UserSubscription() {}
    
    public UserSubscription(Long userId, SubscriptionPlan plan, PaymentProvider provider) {
        this.userId = userId;
        this.plan = plan;
        this.provider = provider;
        this.currentPrice = plan.getMonthlyPrice();
        this.startDate = LocalDateTime.now();
        this.nextBillingDate = LocalDateTime.now().plusMonths(1);
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }
    
    public boolean isCanceled() {
        return status == SubscriptionStatus.CANCELED;
    }
    
    public boolean isPaused() {
        return status == SubscriptionStatus.PAUSED;
    }
    
    public boolean isInTrial() {
        return isTrialActive && trialEndDate != null && trialEndDate.isAfter(LocalDateTime.now());
    }
    
    public boolean hasReachedChallengeLimit() {
        return challengesUsed >= plan.getMaxChallenges();
    }
    
    public boolean hasReachedEvidenceLimit() {
        return evidenceUploadsUsed >= plan.getMaxEvidenceUploads();
    }
    
    public boolean hasReachedStorageLimit() {
        return storageUsedMB >= (plan.getMaxStorageGB() * 1024);
    }
    
    public boolean isInDunning() {
        return dunningStartDate != null && 
               status == SubscriptionStatus.PAST_DUE &&
               failedPaymentAttempts > 0;
    }
    
    public int getDaysInTrial() {
        if (!isInTrial()) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDateTime.now());
    }
    
    public int getDaysUntilTrialEnd() {
        if (!isInTrial()) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), trialEndDate);
    }
    
    // Enums
    public enum SubscriptionStatus {
        ACTIVE,
        CANCELED,
        PAUSED,
        PAST_DUE,
        UNPAID,
        INCOMPLETE,
        TRIALING
    }
    
    public enum BillingCycle {
        MONTHLY,
        YEARLY
    }
    
    public enum PaymentProvider {
        STRIPE,
        PAYPAL,
        MANUAL
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    
    public BillingCycle getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }
    
    public PaymentProvider getProvider() { return provider; }
    public void setProvider(PaymentProvider provider) { this.provider = provider; }
    
    public String getStripeSubscriptionId() { return stripeSubscriptionId; }
    public void setStripeSubscriptionId(String stripeSubscriptionId) { this.stripeSubscriptionId = stripeSubscriptionId; }
    
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }
    
    public String getPaypalSubscriptionId() { return paypalSubscriptionId; }
    public void setPaypalSubscriptionId(String paypalSubscriptionId) { this.paypalSubscriptionId = paypalSubscriptionId; }
    
    public String getPaypalPayerId() { return paypalPayerId; }
    public void setPaypalPayerId(String paypalPayerId) { this.paypalPayerId = paypalPayerId; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public LocalDateTime getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    
    public LocalDateTime getCanceledAt() { return canceledAt; }
    public void setCanceledAt(LocalDateTime canceledAt) { this.canceledAt = canceledAt; }
    
    public LocalDateTime getPausedAt() { return pausedAt; }
    public void setPausedAt(LocalDateTime pausedAt) { this.pausedAt = pausedAt; }
    
    public Boolean getIsBetaUser() { return isBetaUser; }
    public void setIsBetaUser(Boolean isBetaUser) { this.isBetaUser = isBetaUser; }
    
    public Boolean getIsTrialActive() { return isTrialActive; }
    public void setIsTrialActive(Boolean isTrialActive) { this.isTrialActive = isTrialActive; }
    
    public LocalDateTime getTrialEndDate() { return trialEndDate; }
    public void setTrialEndDate(LocalDateTime trialEndDate) { this.trialEndDate = trialEndDate; }
    
    public Integer getChallengesUsed() { return challengesUsed; }
    public void setChallengesUsed(Integer challengesUsed) { this.challengesUsed = challengesUsed; }
    
    public Integer getEvidenceUploadsUsed() { return evidenceUploadsUsed; }
    public void setEvidenceUploadsUsed(Integer evidenceUploadsUsed) { this.evidenceUploadsUsed = evidenceUploadsUsed; }
    
    public Integer getStorageUsedMB() { return storageUsedMB; }
    public void setStorageUsedMB(Integer storageUsedMB) { this.storageUsedMB = storageUsedMB; }
    
    public Integer getFailedPaymentAttempts() { return failedPaymentAttempts; }
    public void setFailedPaymentAttempts(Integer failedPaymentAttempts) { this.failedPaymentAttempts = failedPaymentAttempts; }
    
    public LocalDateTime getLastFailedPayment() { return lastFailedPayment; }
    public void setLastFailedPayment(LocalDateTime lastFailedPayment) { this.lastFailedPayment = lastFailedPayment; }
    
    public LocalDateTime getDunningStartDate() { return dunningStartDate; }
    public void setDunningStartDate(LocalDateTime dunningStartDate) { this.dunningStartDate = dunningStartDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
