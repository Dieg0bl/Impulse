package com.impulse.economy.service;

import com.impulse.economy.Currency;
import com.impulse.economy.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
}
