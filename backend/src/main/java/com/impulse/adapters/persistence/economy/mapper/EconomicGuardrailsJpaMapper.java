package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.EconomicGuardrailsJpaEntity;
import com.impulse.domain.economicguardrails.EconomicGuardrails;
import com.impulse.domain.economicguardrails.EconomicGuardrailsId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EconomicGuardrailsJpaMapper {

    public EconomicGuardrailsJpaEntity toEntity(EconomicGuardrails domain) {
        if (domain == null) {
            return null;
        }

        EconomicGuardrailsJpaEntity entity = new EconomicGuardrailsJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setGuardrailName(domain.getGuardrailName());
        entity.setGuardrailType(domain.getGuardrailType());
        entity.setCurrencyId(domain.getCurrencyId() != null ? UUID.fromString(domain.getCurrencyId()) : null);
        entity.setMinBalance(domain.getMinBalance());
        entity.setMaxBalance(domain.getMaxBalance());
        entity.setMaxDailySpending(domain.getMaxDailySpending());
        entity.setMaxTransactionAmount(domain.getMaxTransactionAmount());
        entity.setMinTransactionAmount(domain.getMinTransactionAmount());
        entity.setFraudDetectionThreshold(domain.getFraudDetectionThreshold());
        entity.setSuspensionThreshold(domain.getSuspensionThreshold());
        entity.setGuardrailConditions(domain.getGuardrailConditions());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public EconomicGuardrails toDomain(EconomicGuardrailsJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return EconomicGuardrails.builder()
                .id(new EconomicGuardrailsId(entity.getId().toString()))
                .guardrailName(entity.getGuardrailName())
                .guardrailType(entity.getGuardrailType())
                .currencyId(entity.getCurrencyId() != null ? entity.getCurrencyId().toString() : null)
                .minBalance(entity.getMinBalance())
                .maxBalance(entity.getMaxBalance())
                .maxDailySpending(entity.getMaxDailySpending())
                .maxTransactionAmount(entity.getMaxTransactionAmount())
                .minTransactionAmount(entity.getMinTransactionAmount())
                .fraudDetectionThreshold(entity.getFraudDetectionThreshold())
                .suspensionThreshold(entity.getSuspensionThreshold())
                .guardrailConditions(entity.getGuardrailConditions())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}