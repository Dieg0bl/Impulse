package com.impulse.economy;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency_transaction")
public class CurrencyTransaction {
    @Id
    private String id;
    private String userId;
    private String type;
    private String currencyId;
    private double amount;
    private String reason;
    private String sourceId;
    @Column(columnDefinition = "TEXT")
    private String metadata;
    private LocalDateTime createdAt;
    private double balanceBefore;
    private double balanceAfter;
    // getters y setters
}
