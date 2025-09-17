package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.EarningRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEarningRuleRepository extends JpaRepository<EarningRuleJpaEntity, UUID> {

    List<EarningRuleJpaEntity> findByIsActiveTrue();

    List<EarningRuleJpaEntity> findByRuleType(String ruleType);

    List<EarningRuleJpaEntity> findByTriggerEvent(String triggerEvent);

    List<EarningRuleJpaEntity> findByCurrencyId(UUID currencyId);

    @Query("SELECT e FROM EarningRuleJpaEntity e WHERE e.isActive = true AND (e.validFrom IS NULL OR e.validFrom <= :now) AND (e.validUntil IS NULL OR e.validUntil >= :now)")
    List<EarningRuleJpaEntity> findActiveRulesAtDate(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM EarningRuleJpaEntity e WHERE e.triggerEvent = :event AND e.isActive = true")
    List<EarningRuleJpaEntity> findActiveRulesByEvent(@Param("event") String triggerEvent);

    @Query("SELECT e FROM EarningRuleJpaEntity e WHERE e.ruleType = :type AND e.currencyId = :currencyId AND e.isActive = true")
    List<EarningRuleJpaEntity> findByTypeAndCurrency(@Param("type") String ruleType, @Param("currencyId") UUID currencyId);
}
