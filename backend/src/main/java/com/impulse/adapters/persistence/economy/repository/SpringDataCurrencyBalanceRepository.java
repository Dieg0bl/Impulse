package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.CurrencyBalanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCurrencyBalanceRepository extends JpaRepository<CurrencyBalanceJpaEntity, UUID> {

    Optional<CurrencyBalanceJpaEntity> findByUserIdAndCurrencyId(UUID userId, UUID currencyId);

    List<CurrencyBalanceJpaEntity> findByUserId(UUID userId);

    List<CurrencyBalanceJpaEntity> findByCurrencyId(UUID currencyId);

    @Query("SELECT c FROM CurrencyBalanceJpaEntity c WHERE c.balance > :minBalance")
    List<CurrencyBalanceJpaEntity> findByBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);

    @Query("SELECT SUM(c.balance) FROM CurrencyBalanceJpaEntity c WHERE c.currencyId = :currencyId")
    BigDecimal getTotalBalanceByCurrency(@Param("currencyId") UUID currencyId);

    @Query("SELECT c FROM CurrencyBalanceJpaEntity c WHERE c.frozenBalance > 0")
    List<CurrencyBalanceJpaEntity> findWithFrozenBalance();
}
