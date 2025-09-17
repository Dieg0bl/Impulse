package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.SpendingItemJpaEntity;
import com.impulse.domain.spendingitem.SpendingItem;
import com.impulse.domain.spendingitem.SpendingItemId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpendingItemJpaMapper {

    public SpendingItemJpaEntity toEntity(SpendingItem domain) {
        if (domain == null) {
            return null;
        }

        SpendingItemJpaEntity entity = new SpendingItemJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setItemName(domain.getItemName());
        entity.setItemType(domain.getItemType());
        entity.setItemCategory(domain.getItemCategory());
        entity.setCurrencyId(UUID.fromString(domain.getCurrencyId()));
        entity.setPrice(domain.getPrice());
        entity.setDescription(domain.getDescription());
        entity.setItemMetadata(domain.getItemMetadata());
        entity.setMaxPurchasesPerUser(domain.getMaxPurchasesPerUser());
        entity.setMaxDailyPurchases(domain.getMaxDailyPurchases());
        entity.setRequiresApproval(domain.getRequiresApproval());
        entity.setIsAvailable(domain.getIsAvailable());
        entity.setAvailableFrom(domain.getAvailableFrom());
        entity.setAvailableUntil(domain.getAvailableUntil());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public SpendingItem toDomain(SpendingItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpendingItem.builder()
                .id(new SpendingItemId(entity.getId().toString()))
                .itemName(entity.getItemName())
                .itemType(entity.getItemType())
                .itemCategory(entity.getItemCategory())
                .currencyId(entity.getCurrencyId().toString())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .itemMetadata(entity.getItemMetadata())
                .maxPurchasesPerUser(entity.getMaxPurchasesPerUser())
                .maxDailyPurchases(entity.getMaxDailyPurchases())
                .requiresApproval(entity.getRequiresApproval())
                .isAvailable(entity.getIsAvailable())
                .availableFrom(entity.getAvailableFrom())
                .availableUntil(entity.getAvailableUntil())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}