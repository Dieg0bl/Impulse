package com.impulse.adapters.persistence.economy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "economic_guardrails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EconomicGuardrailsJpaEntity {

    @Id
    private UUID id;

    @Column(name = "guardrail_name", nullable = false, length = 100)
    private String guardrailName;

    @Column(name = "guardrail_type", nullable = false, length = 50)
    private String guardrailType;

    @Column(name = "currency_id")
    private UUID currencyId;

    @Column(name = "min_balance", precision = 20, scale = 2)
    private BigDecimal minBalance;

    @Column(name = "max_balance", precision = 20, scale = 2)
    private BigDecimal maxBalance;

    @Column(name = "max_daily_spending", precision = 20, scale = 2)
    private BigDecimal maxDailySpending;

    @Column(name = "max_transaction_amount", precision = 20, scale = 2)
    private BigDecimal maxTransactionAmount;

    @Column(name = "min_transaction_amount", precision = 20, scale = 2)
    private BigDecimal minTransactionAmount;

    @Column(name = "fraud_detection_threshold", precision = 20, scale = 2)
    private BigDecimal fraudDetectionThreshold;

    @Column(name = "suspension_threshold", precision = 20, scale = 2)
    private BigDecimal suspensionThreshold;

    @Column(name = "guardrail_conditions", columnDefinition = "JSON")
    private String guardrailConditions;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
