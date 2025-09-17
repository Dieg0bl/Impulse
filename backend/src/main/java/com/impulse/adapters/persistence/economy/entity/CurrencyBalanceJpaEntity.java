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
@Table(name = "currency_balances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "currency_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyBalanceJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "currency_id", nullable = false)
    private UUID currencyId;

    @Column(name = "balance", precision = 20, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "frozen_balance", precision = 20, scale = 2)
    private BigDecimal frozenBalance;

    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (frozenBalance == null) {
            frozenBalance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
