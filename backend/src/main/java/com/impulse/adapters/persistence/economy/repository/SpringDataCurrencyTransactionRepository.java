package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.CurrencyTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCurrencyTransactionRepository extends JpaRepository<CurrencyTransactionJpaEntity, UUID> {

    List<CurrencyTransactionJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<CurrencyTransactionJpaEntity> findByUserIdAndCurrencyIdOrderByCreatedAtDesc(UUID userId, UUID currencyId);

    List<CurrencyTransactionJpaEntity> findByTransactionType(String transactionType);

    @Query("SELECT c FROM CurrencyTransactionJpaEntity c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<CurrencyTransactionJpaEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM CurrencyTransactionJpaEntity c WHERE c.referenceType = :refType AND c.referenceId = :refId")
    List<CurrencyTransactionJpaEntity> findByReference(@Param("refType") String referenceType, @Param("refId") UUID referenceId);

    @Query("SELECT SUM(c.amount) FROM CurrencyTransactionJpaEntity c WHERE c.userId = :userId AND c.currencyId = :currencyId AND c.transactionType = :type")
    BigDecimal getTotalAmountByUserCurrencyAndType(@Param("userId") UUID userId, @Param("currencyId") UUID currencyId, @Param("type") String transactionType);

    @Query("SELECT c FROM CurrencyTransactionJpaEntity c WHERE c.amount > :minAmount ORDER BY c.amount DESC")
    List<CurrencyTransactionJpaEntity> findLargeTransactions(@Param("minAmount") BigDecimal minAmount);
}
