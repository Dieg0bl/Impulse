package com.impulse.economy.controller;

import com.impulse.economy.CurrencyTransaction;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/economy/transactions")
public class CurrencyTransactionController {
    // TODO: Inyectar CurrencyTransactionService

    @GetMapping("/user/{userId}")
    public List<CurrencyTransaction> getUserTransactions(@PathVariable String userId) {
        // TODO: Implementar lógica para devolver transacciones de un usuario
        return List.of();
    }
}
