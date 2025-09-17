package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.SpendingItemJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataSpendingItemRepository;
import com.impulse.application.spendingitem.port.SpendingItemRepository;
import com.impulse.domain.spendingitem.SpendingItem;
import com.impulse.domain.spendingitem.SpendingItemId;
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
public class SpendingItemRepositoryImpl implements SpendingItemRepository {

    private final SpringDataSpendingItemRepository springDataRepository;
    private final SpendingItemJpaMapper mapper;

    @Override
    public SpendingItem save(SpendingItem spendingItem) {
        var entity = mapper.toEntity(spendingItem);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SpendingItem> findById(SpendingItemId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<SpendingItem> findAvailableItems() {
        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findCurrentlyAvailable(now)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpendingItem> findByItemType(String itemType) {
        return springDataRepository.findAvailableByType(itemType)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpendingItem> findByMaxPrice(BigDecimal maxPrice) {
        return springDataRepository.findByMaxPriceAndAvailable(maxPrice)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpendingItem> findByCurrencyId(String currencyId) {
        UUID currencyUuid = UUID.fromString(currencyId);
        return springDataRepository.findByCurrencyId(currencyUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(SpendingItemId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
