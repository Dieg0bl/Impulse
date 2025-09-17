package com.impulse.adapters.persistence.validator;

import com.impulse.adapters.persistence.validator.mapper.ValidatorJpaMapper;
import com.impulse.adapters.persistence.validator.repository.SpringDataValidatorRepository;
import com.impulse.application.validator.port.ValidatorRepository;
import com.impulse.domain.validator.Validator;
import com.impulse.domain.validator.ValidatorId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ValidatorRepositoryImpl implements ValidatorRepository {

    private final SpringDataValidatorRepository springDataRepository;
    private final ValidatorJpaMapper mapper;

    @Override
    public Validator save(Validator validator) {
        var entity = mapper.toEntity(validator);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Validator> findById(ValidatorId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<Validator> findByIsActive(Boolean isActive) {
        return springDataRepository.findByIsActive(isActive)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Validator> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    // Métodos adicionales útiles
    public List<Validator> findActiveValidatorsByRating() {
        return springDataRepository.findByIsActiveTrueOrderByRatingDesc()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Validator> findBySpecialization(String specialization) {
        return springDataRepository.findBySpecializationAndActive(specialization)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public long countActiveValidators() {
        return springDataRepository.countActiveValidators();
    }

    public void deleteById(ValidatorId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
