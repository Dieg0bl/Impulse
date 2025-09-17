package com.impulse.economy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "earning_rule")
public class EarningRule {
    @Id
    private String id;
    private String action;
    private String currencyId;
    private double amount;
    private Integer maxPerDay;
    private Integer maxPerWeek;
    private String conditions;
    // getters y setters
}
