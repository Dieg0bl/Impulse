package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.EconomicGuardrailsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEconomicGuardrailsRepository extends JpaRepository<EconomicGuardrailsJpaEntity, UUID> {

    List<EconomicGuardrailsJpaEntity> findByIsActiveTrue();

    List<EconomicGuardrailsJpaEntity> findByGuardrailType(String guardrailType);

    List<EconomicGuardrailsJpaEntity> findByCurrencyId(UUID currencyId);

    @Query("SELECT e FROM EconomicGuardrailsJpaEntity e WHERE e.guardrailType = :type AND e.isActive = true")
    List<EconomicGuardrailsJpaEntity> findActiveByType(@Param("type") String guardrailType);

    @Query("SELECT e FROM EconomicGuardrailsJpaEntity e WHERE e.currencyId = :currencyId AND e.isActive = true")
    List<EconomicGuardrailsJpaEntity> findActiveByCurrency(@Param("currencyId") UUID currencyId);

    @Query("SELECT e FROM EconomicGuardrailsJpaEntity e WHERE e.guardrailName = :name")
    List<EconomicGuardrailsJpaEntity> findByGuardrailName(@Param("name") String guardrailName);
}
