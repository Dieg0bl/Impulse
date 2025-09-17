package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.CurrencyTransactionJpaEntity;
import com.impulse.domain.currencytransaction.CurrencyTransaction;
import com.impulse.domain.currencytransaction.CurrencyTransactionId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrencyTransactionJpaMapper {

    public CurrencyTransactionJpaEntity toEntity(CurrencyTransaction domain) {
        if (domain == null) {
            return null;
        }

        CurrencyTransactionJpaEntity entity = new CurrencyTransactionJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setUserId(UUID.fromString(domain.getUserId()));
        entity.setCurrencyId(UUID.fromString(domain.getCurrencyId()));
        entity.setTransactionType(domain.getTransactionType());
        entity.setAmount(domain.getAmount());
        entity.setBalanceBefore(domain.getBalanceBefore());
        entity.setBalanceAfter(domain.getBalanceAfter());
        entity.setReferenceType(domain.getReferenceType());
        entity.setReferenceId(domain.getReferenceId() != null ? UUID.fromString(domain.getReferenceId()) : null);
        entity.setDescription(domain.getDescription());
        entity.setTransactionMetadata(domain.getTransactionMetadata());
        entity.setCreatedAt(domain.getCreatedAt());
        
        return entity;
    }

    public CurrencyTransaction toDomain(CurrencyTransactionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CurrencyTransaction.builder()
                .id(new CurrencyTransactionId(entity.getId().toString()))
                .userId(entity.getUserId().toString())
                .currencyId(entity.getCurrencyId().toString())
                .transactionType(entity.getTransactionType())
                .amount(entity.getAmount())
                .balanceBefore(entity.getBalanceBefore())
                .balanceAfter(entity.getBalanceAfter())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId() != null ? entity.getReferenceId().toString() : null)
                .description(entity.getDescription())
                .transactionMetadata(entity.getTransactionMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}