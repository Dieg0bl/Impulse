package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.CurrencyTransactionJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataCurrencyTransactionRepository;
import com.impulse.application.currencytransaction.port.CurrencyTransactionRepository;
import com.impulse.domain.currencytransaction.CurrencyTransaction;
import com.impulse.domain.currencytransaction.CurrencyTransactionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CurrencyTransactionRepositoryImpl implements CurrencyTransactionRepository {

    private final SpringDataCurrencyTransactionRepository springDataRepository;
    private final CurrencyTransactionJpaMapper mapper;

    @Override
    public CurrencyTransaction save(CurrencyTransaction currencyTransaction) {
        var entity = mapper.toEntity(currencyTransaction);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CurrencyTransaction> findById(CurrencyTransactionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<CurrencyTransaction> findByUserId(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return springDataRepository.findByUserIdOrderByCreatedAtDesc(userUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyTransaction> findByUserIdAndCurrencyId(String userId, String currencyId) {
        UUID userUuid = UUID.fromString(userId);
        UUID currencyUuid = UUID.fromString(currencyId);
        return springDataRepository.findByUserIdAndCurrencyIdOrderByCreatedAtDesc(userUuid, currencyUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyTransaction> findByReference(String referenceType, String referenceId) {
        UUID refUuid = UUID.fromString(referenceId);
        return springDataRepository.findByReference(referenceType, refUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyTransaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return springDataRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CurrencyTransactionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
