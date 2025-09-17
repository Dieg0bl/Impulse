package com.impulse.economy.service;

import com.impulse.economy.CurrencyBalance;
import com.impulse.economy.repository.CurrencyBalanceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CurrencyBalanceService {
    private final CurrencyBalanceRepository currencyBalanceRepository;
    public CurrencyBalanceService(CurrencyBalanceRepository currencyBalanceRepository) {
        this.currencyBalanceRepository = currencyBalanceRepository;
    }
    public List<CurrencyBalance> getUserBalances(String userId) {
        return currencyBalanceRepository.findByUserId(userId);
    }
}
