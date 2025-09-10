package com.impulse.domain.model.billing;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidad para planes de suscripción
 */
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String planCode; // BASIC, PRO, TEAMS
    
    @NotBlank
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal monthlyPrice;
    
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal yearlyPrice;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isPopular = false;
    
    // Limitaciones del plan
    @NotNull
    @Min(1)
    private Integer maxChallenges = 10;
    
    @NotNull
    @Min(1)
    private Integer maxEvidenceUploads = 50;
    
    @NotNull
    @Min(1)
    private Integer maxStorageGB = 5;
    
    @Column(nullable = false)
    private Boolean hasCoachAccess = false;
    
    @Column(nullable = false)
    private Boolean hasAdvancedAnalytics = false;
    
    @Column(nullable = false)
    private Boolean hasApiAccess = false;
    
    @Column(nullable = false)
    private Boolean hasPrioritySupport = false;
    
    // IDs externos para pasarelas de pago
    private String stripeMonthlyPriceId;
    private String stripeYearlyPriceId;
    private String paypalMonthlyPlanId;
    private String paypalYearlyPlanId;
    
    // Metadatos
    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private Set<String> features;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public SubscriptionPlan() {}
    
    public SubscriptionPlan(String planCode, String name, BigDecimal monthlyPrice, BigDecimal yearlyPrice) {
        this.planCode = planCode;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isFree() {
        return monthlyPrice.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public BigDecimal getYearlyDiscount() {
        if (yearlyPrice == null || monthlyPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal yearlyEquivalent = monthlyPrice.multiply(new BigDecimal("12"));
        BigDecimal savings = yearlyEquivalent.subtract(yearlyPrice);
        return savings.divide(yearlyEquivalent, 4, java.math.RoundingMode.HALF_UP)
                     .multiply(new BigDecimal("100"));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    
    public BigDecimal getYearlyPrice() { return yearlyPrice; }
    public void setYearlyPrice(BigDecimal yearlyPrice) { this.yearlyPrice = yearlyPrice; }
    
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
    
    public String getStripeMonthlyPriceId() { return stripeMonthlyPriceId; }
    public void setStripeMonthlyPriceId(String stripeMonthlyPriceId) { this.stripeMonthlyPriceId = stripeMonthlyPriceId; }
    
    public String getStripeYearlyPriceId() { return stripeYearlyPriceId; }
    public void setStripeYearlyPriceId(String stripeYearlyPriceId) { this.stripeYearlyPriceId = stripeYearlyPriceId; }
    
    public String getPaypalMonthlyPlanId() { return paypalMonthlyPlanId; }
    public void setPaypalMonthlyPlanId(String paypalMonthlyPlanId) { this.paypalMonthlyPlanId = paypalMonthlyPlanId; }
    
    public String getPaypalYearlyPlanId() { return paypalYearlyPlanId; }
    public void setPaypalYearlyPlanId(String paypalYearlyPlanId) { this.paypalYearlyPlanId = paypalYearlyPlanId; }
    
    public Set<String> getFeatures() { return features; }
    public void setFeatures(Set<String> features) { this.features = features; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
