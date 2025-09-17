package com.impulse.economy.repository;

import com.impulse.economy.CurrencyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, String> {
    List<CurrencyTransaction> findByUserId(String userId);
}
