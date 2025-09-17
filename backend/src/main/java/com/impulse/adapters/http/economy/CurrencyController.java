package com.impulse.economy.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.economy.Currency;

@RestController
@RequestMapping("/api/economy/currencies")
public class CurrencyController {
    // TODO: Inyectar CurrencyService

    @GetMapping
    public List<Currency> getAllCurrencies() {
        // TODO: Implementar lógica para devolver todas las monedas
        return List.of();
    }
}
