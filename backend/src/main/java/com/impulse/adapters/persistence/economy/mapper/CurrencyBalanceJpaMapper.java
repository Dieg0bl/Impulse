package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.CurrencyBalanceJpaEntity;
import com.impulse.domain.currencybalance.CurrencyBalance;
import com.impulse.domain.currencybalance.CurrencyBalanceId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrencyBalanceJpaMapper {

    public CurrencyBalanceJpaEntity toEntity(CurrencyBalance domain) {
        if (domain == null) {
            return null;
        }

        CurrencyBalanceJpaEntity entity = new CurrencyBalanceJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setUserId(UUID.fromString(domain.getUserId()));
        entity.setCurrencyId(UUID.fromString(domain.getCurrencyId()));
        entity.setBalance(domain.getBalance());
        entity.setFrozenBalance(domain.getFrozenBalance());
        entity.setLastTransactionAt(domain.getLastTransactionAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public CurrencyBalance toDomain(CurrencyBalanceJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CurrencyBalance.builder()
                .id(new CurrencyBalanceId(entity.getId().toString()))
                .userId(entity.getUserId().toString())
                .currencyId(entity.getCurrencyId().toString())
                .balance(entity.getBalance())
                .frozenBalance(entity.getFrozenBalance())
                .lastTransactionAt(entity.getLastTransactionAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}