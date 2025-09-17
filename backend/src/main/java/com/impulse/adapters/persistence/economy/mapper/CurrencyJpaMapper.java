package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.CurrencyJpaEntity;
import com.impulse.domain.currency.Currency;
import com.impulse.domain.currency.CurrencyId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrencyJpaMapper {

    public CurrencyJpaEntity toEntity(Currency domain) {
        if (domain == null) {
            return null;
        }

        CurrencyJpaEntity entity = new CurrencyJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setCurrencyCode(domain.getCurrencyCode());
        entity.setCurrencyName(domain.getCurrencyName());
        entity.setCurrencySymbol(domain.getCurrencySymbol());
        entity.setExchangeRate(domain.getExchangeRate());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Currency toDomain(CurrencyJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Currency.builder()
                .id(new CurrencyId(entity.getId().toString()))
                .currencyCode(entity.getCurrencyCode())
                .currencyName(entity.getCurrencyName())
                .currencySymbol(entity.getCurrencySymbol())
                .exchangeRate(entity.getExchangeRate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}