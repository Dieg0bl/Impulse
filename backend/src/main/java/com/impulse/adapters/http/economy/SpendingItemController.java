package com.impulse.economy.controller;

import com.impulse.economy.SpendingItem;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/economy/spending-items")
public class SpendingItemController {
    // TODO: Inyectar SpendingItemService

    @GetMapping
    public List<SpendingItem> getAllSpendingItems() {
        // TODO: Implementar lógica para devolver todos los spending items
        return List.of();
    }
}
