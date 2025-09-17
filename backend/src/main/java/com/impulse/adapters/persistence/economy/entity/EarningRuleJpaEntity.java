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
@Table(name = "earning_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EarningRuleJpaEntity {

    @Id
    private UUID id;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    @Column(name = "trigger_event", nullable = false, length = 100)
    private String triggerEvent;

    @Column(name = "currency_id", nullable = false)
    private UUID currencyId;

    @Column(name = "amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "max_daily_earnings", precision = 20, scale = 2)
    private BigDecimal maxDailyEarnings;

    @Column(name = "max_total_earnings", precision = 20, scale = 2)
    private BigDecimal maxTotalEarnings;

    @Column(name = "rule_conditions", columnDefinition = "JSON")
    private String ruleConditions;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

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
