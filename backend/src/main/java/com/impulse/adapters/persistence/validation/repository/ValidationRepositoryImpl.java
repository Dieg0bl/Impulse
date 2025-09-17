package com.impulse.adapters.persistence.validation.repository;

import com.impulse.application.validation.port.ValidationRepository;
import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationId;
import com.impulse.domain.enums.ValidationStatus;
import com.impulse.adapters.persistence.validation.entity.ValidationJpaEntity;
import com.impulse.adapters.persistence.validation.mapper.ValidationJpaMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class ValidationRepositoryImpl implements ValidationRepository {
    private final SpringDataValidationRepository springDataValidationRepository;
    private final ValidationJpaMapper validationJpaMapper;

    public ValidationRepositoryImpl(SpringDataValidationRepository springDataValidationRepository, ValidationJpaMapper validationJpaMapper) {
        this.springDataValidationRepository = springDataValidationRepository;
        this.validationJpaMapper = validationJpaMapper;
    }

    @Override
    public Validation save(Validation validation) {
        ValidationJpaEntity entity = validationJpaMapper.toEntity(validation);
        ValidationJpaEntity saved = springDataValidationRepository.save(entity);
        return validationJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Validation> findById(ValidationId id) {
        return springDataValidationRepository.findById(id.getValue())
                .map(validationJpaMapper::toDomain);
    }

    @Override
    public long countByStatus(ValidationStatus status) {
        return springDataValidationRepository.countByStatus(status);
    }
}


