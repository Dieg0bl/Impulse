package com.impulse.adapters.persistence.economy.mapper;

import com.impulse.adapters.persistence.economy.entity.EarningRuleJpaEntity;
import com.impulse.domain.earningrule.EarningRule;
import com.impulse.domain.earningrule.EarningRuleId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EarningRuleJpaMapper {

    public EarningRuleJpaEntity toEntity(EarningRule domain) {
        if (domain == null) {
            return null;
        }

        EarningRuleJpaEntity entity = new EarningRuleJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setRuleName(domain.getRuleName());
        entity.setRuleType(domain.getRuleType());
        entity.setTriggerEvent(domain.getTriggerEvent());
        entity.setCurrencyId(UUID.fromString(domain.getCurrencyId()));
        entity.setAmount(domain.getAmount());
        entity.setMaxDailyEarnings(domain.getMaxDailyEarnings());
        entity.setMaxTotalEarnings(domain.getMaxTotalEarnings());
        entity.setRuleConditions(domain.getRuleConditions());
        entity.setIsActive(domain.getIsActive());
        entity.setValidFrom(domain.getValidFrom());
        entity.setValidUntil(domain.getValidUntil());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public EarningRule toDomain(EarningRuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return EarningRule.builder()
                .id(new EarningRuleId(entity.getId().toString()))
                .ruleName(entity.getRuleName())
                .ruleType(entity.getRuleType())
                .triggerEvent(entity.getTriggerEvent())
                .currencyId(entity.getCurrencyId().toString())
                .amount(entity.getAmount())
                .maxDailyEarnings(entity.getMaxDailyEarnings())
                .maxTotalEarnings(entity.getMaxTotalEarnings())
                .ruleConditions(entity.getRuleConditions())
                .isActive(entity.getIsActive())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}