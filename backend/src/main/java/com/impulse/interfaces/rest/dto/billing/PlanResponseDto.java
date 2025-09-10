package com.impulse.interfaces.rest.dto.billing;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO para respuesta de planes de suscripción
 */
public class PlanResponseDto {
    
    private String planCode;
    private String name;
    private String description;
    private String monthlyPrice;
    private String yearlyPrice;
    private String currency = "EUR";
    private Boolean isActive;
    private Boolean isPopular;
    
    // Limitaciones del plan
    private Integer maxChallenges;
    private Integer maxEvidenceUploads;
    private Integer maxStorageGB;
    private Boolean hasCoachAccess;
    private Boolean hasAdvancedAnalytics;
    private Boolean hasApiAccess;
    private Boolean hasPrioritySupport;
    
    // Lista de características
    private List<String> features;
    
    // IDs de las pasarelas de pago
    private String stripeMonthlyPriceId;
    private String stripeYearlyPriceId;
    private String paypalMonthlyPlanId;
    private String paypalYearlyPlanId;
    
    // Constructors
    public PlanResponseDto() {}
    
    public PlanResponseDto(String planCode, String name, String monthlyPrice, String yearlyPrice) {
        this.planCode = planCode;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
    }
    
    // Business methods
    public boolean isFree() {
        return "0.00".equals(monthlyPrice) || "0".equals(monthlyPrice);
    }
    
    // Getters and Setters
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(String monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    
    public String getYearlyPrice() { return yearlyPrice; }
    public void setYearlyPrice(String yearlyPrice) { this.yearlyPrice = yearlyPrice; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsPopular() { return isPopular; }
    public void setIsPopular(Boolean isPopular) { this.isPopular = isPopular; }
    
    public Integer getMaxChallenges() { return maxChallenges; }
    public void setMaxChallenges(Integer maxChallenges) { this.maxChallenges = maxChallenges; }
    
    public Integer getMaxEvidenceUploads() { return maxEvidenceUploads; }
    public void setMaxEvidenceUploads(Integer maxEvidenceUploads) { this.maxEvidenceUploads = maxEvidenceUploads; }
    
    public Integer getMaxStorageGB() { return maxStorageGB; }
    public void setMaxStorageGB(Integer maxStorageGB) { this.maxStorageGB = maxStorageGB; }
    
    public Boolean getHasCoachAccess() { return hasCoachAccess; }
    public void setHasCoachAccess(Boolean hasCoachAccess) { this.hasCoachAccess = hasCoachAccess; }
    
    public Boolean getHasAdvancedAnalytics() { return hasAdvancedAnalytics; }
    public void setHasAdvancedAnalytics(Boolean hasAdvancedAnalytics) { this.hasAdvancedAnalytics = hasAdvancedAnalytics; }
    
    public Boolean getHasApiAccess() { return hasApiAccess; }
    public void setHasApiAccess(Boolean hasApiAccess) { this.hasApiAccess = hasApiAccess; }
    
    public Boolean getHasPrioritySupport() { return hasPrioritySupport; }
    public void setHasPrioritySupport(Boolean hasPrioritySupport) { this.hasPrioritySupport = hasPrioritySupport; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public String getStripeMonthlyPriceId() { return stripeMonthlyPriceId; }
    public void setStripeMonthlyPriceId(String stripeMonthlyPriceId) { this.stripeMonthlyPriceId = stripeMonthlyPriceId; }
    
    public String getStripeYearlyPriceId() { return stripeYearlyPriceId; }
    public void setStripeYearlyPriceId(String stripeYearlyPriceId) { this.stripeYearlyPriceId = stripeYearlyPriceId; }
    
    public String getPaypalMonthlyPlanId() { return paypalMonthlyPlanId; }
    public void setPaypalMonthlyPlanId(String paypalMonthlyPlanId) { this.paypalMonthlyPlanId = paypalMonthlyPlanId; }
    
    public String getPaypalYearlyPlanId() { return paypalYearlyPlanId; }
    public void setPaypalYearlyPlanId(String paypalYearlyPlanId) { this.paypalYearlyPlanId = paypalYearlyPlanId; }
}
