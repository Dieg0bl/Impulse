package com.impulse.interfaces.rest.dto.billing;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de suscripción
 */
public class SubscriptionResponseDto {
    
    private Long id;
    private String planCode;
    private String planName;
    private String status;
    private String billingCycle;
    private String provider;
    private String currentPrice;
    private String currency;
    
    // Fechas importantes
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private LocalDateTime canceledAt;
    
    // Trial y beta
    private Boolean isTrialActive;
    private LocalDateTime trialEndDate;
    private Integer daysInTrial;
    private Integer daysUntilTrialEnd;
    private Boolean isBetaUser;
    
    // Uso actual
    private Integer challengesUsed;
    private Integer maxChallenges;
    private Integer evidenceUploadsUsed;
    private Integer maxEvidenceUploads;
    private Integer storageUsedMB;
    private Integer maxStorageGB;
    
    // Estado de la suscripción
    private Boolean cancelAtPeriodEnd;
    private Integer failedPaymentAttempts;
    private LocalDateTime lastFailedPayment;
    private Boolean isInDunning;
    
    // URLs de gestión
    private String customerPortalUrl;
    private String invoicesUrl;
    
    // Constructors
    public SubscriptionResponseDto() {}
    
    // Business methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isCanceled() {
        return "CANCELED".equals(status);
    }
    
    public boolean hasReachedChallengeLimit() {
        return challengesUsed != null && maxChallenges != null && challengesUsed >= maxChallenges;
    }
    
    public boolean hasReachedEvidenceLimit() {
        return evidenceUploadsUsed != null && maxEvidenceUploads != null && evidenceUploadsUsed >= maxEvidenceUploads;
    }
    
    public boolean hasReachedStorageLimit() {
        return storageUsedMB != null && maxStorageGB != null && storageUsedMB >= (maxStorageGB * 1024);
    }
    
    public double getStorageUsagePercentage() {
        if (storageUsedMB == null || maxStorageGB == null || maxStorageGB == 0) {
            return 0.0;
        }
        return (storageUsedMB.doubleValue() / (maxStorageGB * 1024)) * 100;
    }
    
    public double getChallengeUsagePercentage() {
        if (challengesUsed == null || maxChallenges == null || maxChallenges == 0) {
            return 0.0;
        }
        return (challengesUsed.doubleValue() / maxChallenges) * 100;
    }
    
    public double getEvidenceUsagePercentage() {
        if (evidenceUploadsUsed == null || maxEvidenceUploads == null || maxEvidenceUploads == 0) {
            return 0.0;
        }
        return (evidenceUploadsUsed.doubleValue() / maxEvidenceUploads) * 100;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(String currentPrice) { this.currentPrice = currentPrice; }
    
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
    
    public Boolean getIsTrialActive() { return isTrialActive; }
    public void setIsTrialActive(Boolean isTrialActive) { this.isTrialActive = isTrialActive; }
    
    public LocalDateTime getTrialEndDate() { return trialEndDate; }
    public void setTrialEndDate(LocalDateTime trialEndDate) { this.trialEndDate = trialEndDate; }
    
    public Integer getDaysInTrial() { return daysInTrial; }
    public void setDaysInTrial(Integer daysInTrial) { this.daysInTrial = daysInTrial; }
    
    public Integer getDaysUntilTrialEnd() { return daysUntilTrialEnd; }
    public void setDaysUntilTrialEnd(Integer daysUntilTrialEnd) { this.daysUntilTrialEnd = daysUntilTrialEnd; }
    
    public Boolean getIsBetaUser() { return isBetaUser; }
    public void setIsBetaUser(Boolean isBetaUser) { this.isBetaUser = isBetaUser; }
    
    public Integer getChallengesUsed() { return challengesUsed; }
    public void setChallengesUsed(Integer challengesUsed) { this.challengesUsed = challengesUsed; }
    
    public Integer getMaxChallenges() { return maxChallenges; }
    public void setMaxChallenges(Integer maxChallenges) { this.maxChallenges = maxChallenges; }
    
    public Integer getEvidenceUploadsUsed() { return evidenceUploadsUsed; }
    public void setEvidenceUploadsUsed(Integer evidenceUploadsUsed) { this.evidenceUploadsUsed = evidenceUploadsUsed; }
    
    public Integer getMaxEvidenceUploads() { return maxEvidenceUploads; }
    public void setMaxEvidenceUploads(Integer maxEvidenceUploads) { this.maxEvidenceUploads = maxEvidenceUploads; }
    
    public Integer getStorageUsedMB() { return storageUsedMB; }
    public void setStorageUsedMB(Integer storageUsedMB) { this.storageUsedMB = storageUsedMB; }
    
    public Integer getMaxStorageGB() { return maxStorageGB; }
    public void setMaxStorageGB(Integer maxStorageGB) { this.maxStorageGB = maxStorageGB; }
    
    public Boolean getCancelAtPeriodEnd() { return cancelAtPeriodEnd; }
    public void setCancelAtPeriodEnd(Boolean cancelAtPeriodEnd) { this.cancelAtPeriodEnd = cancelAtPeriodEnd; }
    
    public Integer getFailedPaymentAttempts() { return failedPaymentAttempts; }
    public void setFailedPaymentAttempts(Integer failedPaymentAttempts) { this.failedPaymentAttempts = failedPaymentAttempts; }
    
    public LocalDateTime getLastFailedPayment() { return lastFailedPayment; }
    public void setLastFailedPayment(LocalDateTime lastFailedPayment) { this.lastFailedPayment = lastFailedPayment; }
    
    public Boolean getIsInDunning() { return isInDunning; }
    public void setIsInDunning(Boolean isInDunning) { this.isInDunning = isInDunning; }
    
    public String getCustomerPortalUrl() { return customerPortalUrl; }
    public void setCustomerPortalUrl(String customerPortalUrl) { this.customerPortalUrl = customerPortalUrl; }
    
    public String getInvoicesUrl() { return invoicesUrl; }
    public void setInvoicesUrl(String invoicesUrl) { this.invoicesUrl = invoicesUrl; }
}
