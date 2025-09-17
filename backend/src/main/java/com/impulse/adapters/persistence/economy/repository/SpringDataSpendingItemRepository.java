package com.impulse.adapters.persistence.economy.repository;

import com.impulse.adapters.persistence.economy.entity.SpendingItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSpendingItemRepository extends JpaRepository<SpendingItemJpaEntity, UUID> {

    List<SpendingItemJpaEntity> findByIsAvailableTrue();

    List<SpendingItemJpaEntity> findByItemType(String itemType);

    List<SpendingItemJpaEntity> findByItemCategory(String itemCategory);

    List<SpendingItemJpaEntity> findByCurrencyId(UUID currencyId);

    @Query("SELECT s FROM SpendingItemJpaEntity s WHERE s.price <= :maxPrice AND s.isAvailable = true")
    List<SpendingItemJpaEntity> findByMaxPriceAndAvailable(@Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT s FROM SpendingItemJpaEntity s WHERE s.itemType = :type AND s.isAvailable = true")
    List<SpendingItemJpaEntity> findAvailableByType(@Param("type") String itemType);

    @Query("SELECT s FROM SpendingItemJpaEntity s WHERE s.isAvailable = true AND (s.availableFrom IS NULL OR s.availableFrom <= :now) AND (s.availableUntil IS NULL OR s.availableUntil >= :now)")
    List<SpendingItemJpaEntity> findCurrentlyAvailable(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM SpendingItemJpaEntity s WHERE s.requiresApproval = true AND s.isAvailable = true")
    List<SpendingItemJpaEntity> findRequiringApproval();
}
