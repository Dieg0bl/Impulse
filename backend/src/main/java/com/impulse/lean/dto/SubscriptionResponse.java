package com.impulse.lean.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    
    private Long id;
    private Long userId;
    private String subscriptionPlan;
    private String status;
    private String billingCycle;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private LocalDateTime trialStartDate;
    private LocalDateTime trialEndDate;
    private Boolean autoRenew;
    private BigDecimal discountPercentage;
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    // Campos calculados
    private Boolean isActive;
    private Boolean isTrial;
    private Boolean isExpired;
    private Long daysUntilExpiry;
    private BigDecimal effectiveAmount;
}
