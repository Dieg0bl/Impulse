package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.EarningRuleJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataEarningRuleRepository;
import com.impulse.application.earningrule.port.EarningRuleRepository;
import com.impulse.domain.earningrule.EarningRule;
import com.impulse.domain.earningrule.EarningRuleId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EarningRuleRepositoryImpl implements EarningRuleRepository {

    private final SpringDataEarningRuleRepository springDataRepository;
    private final EarningRuleJpaMapper mapper;

    @Override
    public EarningRule save(EarningRule earningRule) {
        var entity = mapper.toEntity(earningRule);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EarningRule> findById(EarningRuleId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<EarningRule> findActiveRules() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EarningRule> findByTriggerEvent(String triggerEvent) {
        return springDataRepository.findActiveRulesByEvent(triggerEvent)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EarningRule> findActiveRulesAtDate(LocalDateTime date) {
        return springDataRepository.findActiveRulesAtDate(date)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EarningRule> findByCurrencyId(String currencyId) {
        UUID currencyUuid = UUID.fromString(currencyId);
        return springDataRepository.findByCurrencyId(currencyUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(EarningRuleId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
