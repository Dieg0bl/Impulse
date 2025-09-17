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
@Table(name = "spending_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendingItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_type", nullable = false, length = 50)
    private String itemType;

    @Column(name = "item_category", length = 50)
    private String itemCategory;

    @Column(name = "currency_id", nullable = false)
    private UUID currencyId;

    @Column(name = "price", precision = 20, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "item_metadata", columnDefinition = "JSON")
    private String itemMetadata;

    @Column(name = "max_purchases_per_user")
    private Integer maxPurchasesPerUser;

    @Column(name = "max_daily_purchases")
    private Integer maxDailyPurchases;

    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (requiresApproval == null) {
            requiresApproval = false;
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}