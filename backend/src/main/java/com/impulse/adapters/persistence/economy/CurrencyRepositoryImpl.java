package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.CurrencyJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataCurrencyRepository;
import com.impulse.application.currency.port.CurrencyRepository;
import com.impulse.domain.currency.Currency;
import com.impulse.domain.currency.CurrencyId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CurrencyRepositoryImpl implements CurrencyRepository {

    private final SpringDataCurrencyRepository springDataRepository;
    private final CurrencyJpaMapper mapper;

    @Override
    public Currency save(Currency currency) {
        var entity = mapper.toEntity(currency);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Currency> findById(CurrencyId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Currency> findByCurrencyCode(String currencyCode) {
        return springDataRepository.findByCurrencyCode(currencyCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<Currency> findActiveCurrencies() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCurrencyCode(String currencyCode) {
        return springDataRepository.existsByCurrencyCode(currencyCode);
    }

    @Override
    public void deleteById(CurrencyId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
