package com.impulse.economy;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency_balance")
@IdClass(CurrencyBalanceId.class)
public class CurrencyBalance {
    @Id
    private String userId;
    @Id
    private String currencyId;
    private double amount;
    private LocalDateTime lastUpdated;
    // getters y setters
}
