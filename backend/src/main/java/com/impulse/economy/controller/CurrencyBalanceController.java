package com.impulse.economy.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.economy.CurrencyBalance;

@RestController
@RequestMapping("/api/economy/balances")
public class CurrencyBalanceController {
    // TODO: Inyectar CurrencyBalanceService

    @GetMapping("/{userId}")
    public List<CurrencyBalance> getUserBalances(@PathVariable String userId) {
        // TODO: Implementar lógica para devolver los saldos de un usuario
        return List.of();
    }
}
