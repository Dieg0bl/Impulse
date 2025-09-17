package com.impulse.economy.repository;

import com.impulse.economy.CurrencyBalance;
import com.impulse.economy.CurrencyBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrencyBalanceRepository extends JpaRepository<CurrencyBalance, CurrencyBalanceId> {
    List<CurrencyBalance> findByUserId(String userId);
}
