package com.impulse.adapters.persistence.economy;

import com.impulse.adapters.persistence.economy.mapper.EconomicGuardrailsJpaMapper;
import com.impulse.adapters.persistence.economy.repository.SpringDataEconomicGuardrailsRepository;
import com.impulse.application.economicguardrails.port.EconomicGuardrailsRepository;
import com.impulse.domain.economicguardrails.EconomicGuardrails;
import com.impulse.domain.economicguardrails.EconomicGuardrailsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EconomicGuardrailsRepositoryImpl implements EconomicGuardrailsRepository {

    private final SpringDataEconomicGuardrailsRepository springDataRepository;
    private final EconomicGuardrailsJpaMapper mapper;

    @Override
    public EconomicGuardrails save(EconomicGuardrails economicGuardrails) {
        var entity = mapper.toEntity(economicGuardrails);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EconomicGuardrails> findById(EconomicGuardrailsId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<EconomicGuardrails> findActiveGuardrails() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EconomicGuardrails> findByGuardrailType(String guardrailType) {
        return springDataRepository.findActiveByType(guardrailType)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EconomicGuardrails> findByCurrencyId(String currencyId) {
        UUID currencyUuid = UUID.fromString(currencyId);
        return springDataRepository.findActiveByCurrency(currencyUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(EconomicGuardrailsId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
