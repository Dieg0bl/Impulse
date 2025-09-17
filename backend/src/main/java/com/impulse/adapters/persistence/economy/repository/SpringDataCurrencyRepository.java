package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.CurrencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCurrencyRepository extends JpaRepository<CurrencyJpaEntity, UUID> {

    Optional<CurrencyJpaEntity> findByCurrencyCode(String currencyCode);

    List<CurrencyJpaEntity> findByIsActiveTrue();

    @Query("SELECT c FROM CurrencyJpaEntity c WHERE c.currencyName LIKE %:name%")
    List<CurrencyJpaEntity> findByCurrencyNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(c) FROM CurrencyJpaEntity c WHERE c.isActive = true")
    long countActiveCurrencies();

    boolean existsByCurrencyCode(String currencyCode);
}
