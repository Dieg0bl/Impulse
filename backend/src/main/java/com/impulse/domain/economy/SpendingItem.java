package com.impulse.economy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spending_item")
public class SpendingItem {
    @Id
    private String id;
    private String name;
    private String category;
    private String currencyId;
    private double cost;
    private String description;
    private Double realCost;
    private Integer maxPurchasesPerMonth;
    // getters y setters
}
