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
@Table(name = "currency_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTransactionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "currency_id", nullable = false)
    private UUID currencyId;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @Column(name = "amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 20, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 20, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "transaction_metadata", columnDefinition = "JSON")
    private String transactionMetadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
