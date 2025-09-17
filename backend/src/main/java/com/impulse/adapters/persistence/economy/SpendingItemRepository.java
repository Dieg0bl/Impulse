package com.impulse.economy.repository;

import com.impulse.economy.SpendingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpendingItemRepository extends JpaRepository<SpendingItem, String> {
}
