package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.CurrencyBalanceJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataCurrencyBalanceRepository;
import com.impulse.application.currencybalance.port.CurrencyBalanceRepository;
import com.impulse.domain.currencybalance.CurrencyBalance;
import com.impulse.domain.currencybalance.CurrencyBalanceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CurrencyBalanceRepositoryImpl implements CurrencyBalanceRepository {

    private final SpringDataCurrencyBalanceRepository springDataRepository;
    private final CurrencyBalanceJpaMapper mapper;

    @Override
    public CurrencyBalance save(CurrencyBalance currencyBalance) {
        var entity = mapper.toEntity(currencyBalance);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CurrencyBalance> findById(CurrencyBalanceId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<CurrencyBalance> findByUserIdAndCurrencyId(String userId, String currencyId) {
        UUID userUuid = UUID.fromString(userId);
        UUID currencyUuid = UUID.fromString(currencyId);
        return springDataRepository.findByUserIdAndCurrencyId(userUuid, currencyUuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<CurrencyBalance> findByUserId(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return springDataRepository.findByUserId(userUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalBalanceByCurrency(String currencyId) {
        UUID currencyUuid = UUID.fromString(currencyId);
        BigDecimal total = springDataRepository.getTotalBalanceByCurrency(currencyUuid);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public void deleteById(CurrencyBalanceId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
