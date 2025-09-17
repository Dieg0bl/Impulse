package com.impulse.economy.service;

import com.impulse.economy.SpendingItem;
import com.impulse.economy.repository.SpendingItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpendingItemService {
    private final SpendingItemRepository spendingItemRepository;
    public SpendingItemService(SpendingItemRepository spendingItemRepository) {
        this.spendingItemRepository = spendingItemRepository;
    }
    public List<SpendingItem> getAllSpendingItems() {
        return spendingItemRepository.findAll();
    }
}
