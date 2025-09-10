package com.impulse.interfaces.rest.dto.billing;

import jakarta.validation.constraints.*;

/**
 * DTO para solicitud de checkout
 */
public class CheckoutRequestDto {
    
    @NotBlank(message = "Plan code is required")
    private String planCode;
    
    @NotBlank(message = "Billing cycle is required")
    @Pattern(regexp = "MONTHLY|YEARLY", message = "Billing cycle must be MONTHLY or YEARLY")
    private String billingCycle;
    
    @Pattern(regexp = "STRIPE|PAYPAL", message = "Provider must be STRIPE or PAYPAL")
    private String preferredProvider;
    
    private String successUrl;
    private String cancelUrl;
    
    // Metadatos adicionales
    private String couponCode;
    private String referralCode;
    private Boolean allowPromotionalEmails;
    
    // Para trial específico
    private Integer customTrialDays;
    
    // Constructors
    public CheckoutRequestDto() {
        // DTO constructor vacío requerido por Jackson
    }
    
    public CheckoutRequestDto(String planCode, String billingCycle) {
        this.planCode = planCode;
        this.billingCycle = billingCycle;
    }
    
    // Validation methods
    public boolean isValid() {
        return planCode != null && !planCode.trim().isEmpty() &&
               billingCycle != null && (billingCycle.equals("MONTHLY") || billingCycle.equals("YEARLY"));
    }
    
    // Business methods
    public boolean hasCustomTrial() {
        return customTrialDays != null && customTrialDays > 0;
    }
    
    public boolean hasCoupon() {
        return couponCode != null && !couponCode.trim().isEmpty();
    }
    
    public boolean hasReferral() {
        return referralCode != null && !referralCode.trim().isEmpty();
    }
    
    public String getEffectiveProvider() {
        return preferredProvider != null ? preferredProvider : "STRIPE";
    }
    
    // Getters and Setters
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    
    public String getPreferredProvider() { return preferredProvider; }
    public void setPreferredProvider(String preferredProvider) { this.preferredProvider = preferredProvider; }
    
    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }
    
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    
    public Boolean getAllowPromotionalEmails() { return allowPromotionalEmails; }
    public void setAllowPromotionalEmails(Boolean allowPromotionalEmails) { this.allowPromotionalEmails = allowPromotionalEmails; }
    
    public Integer getCustomTrialDays() { return customTrialDays; }
    public void setCustomTrialDays(Integer customTrialDays) { this.customTrialDays = customTrialDays; }
}
