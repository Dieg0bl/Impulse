package com.impulse.economy.service;

import com.impulse.economy.CurrencyTransaction;
import com.impulse.economy.repository.CurrencyTransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CurrencyTransactionService {
    private final CurrencyTransactionRepository currencyTransactionRepository;
    public CurrencyTransactionService(CurrencyTransactionRepository currencyTransactionRepository) {
        this.currencyTransactionRepository = currencyTransactionRepository;
    }
    public List<CurrencyTransaction> getUserTransactions(String userId) {
        return currencyTransactionRepository.findByUserId(userId);
    }
}
