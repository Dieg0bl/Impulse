package com.impulse.adapters.persistence.evidencevalidation;

import com.impulse.adapters.persistence.evidencevalidation.mapper.EvidenceValidationJpaMapper;
import com.impulse.adapters.persistence.evidencevalidation.repository.SpringDataEvidenceValidationRepository;
import com.impulse.application.evidencevalidation.port.EvidenceValidationRepository;
import com.impulse.domain.evidencevalidation.EvidenceValidation;
import com.impulse.domain.evidencevalidation.EvidenceValidationId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EvidenceValidationRepositoryImpl implements EvidenceValidationRepository {

    private final SpringDataEvidenceValidationRepository springDataRepository;
    private final EvidenceValidationJpaMapper mapper;

    @Override
    public EvidenceValidation save(EvidenceValidation evidenceValidation) {
        var entity = mapper.toEntity(evidenceValidation);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EvidenceValidation> findById(EvidenceValidationId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<EvidenceValidation> findByEvidenceId(String evidenceId) {
        UUID evidenceUuid = UUID.fromString(evidenceId);
        return springDataRepository.findByEvidenceIdOrderByValidatedAtDesc(evidenceUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvidenceValidation> findByValidatorId(String validatorId) {
        UUID validatorUuid = UUID.fromString(validatorId);
        return springDataRepository.findByValidatorIdOrderByValidatedAtDesc(validatorUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvidenceValidation> findByStatus(String status) {
        return springDataRepository.findByStatus(status, Pageable.unpaged())
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EvidenceValidation> findByStatus(String status, Pageable pageable) {
        return springDataRepository.findByStatus(status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<EvidenceValidation> findPendingValidations(Pageable pageable) {
        return springDataRepository.findPending(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<EvidenceValidation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return springDataRepository.findByDateRange(startDate, endDate, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countByValidatorId(String validatorId) {
        UUID validatorUuid = UUID.fromString(validatorId);
        return springDataRepository.countByValidatorId(validatorUuid);
    }

    @Override
    public long countByEvidenceId(String evidenceId) {
        UUID evidenceUuid = UUID.fromString(evidenceId);
        return springDataRepository.countByEvidenceId(evidenceUuid);
    }

    @Override
    public Double getAverageScoreByEvidence(String evidenceId) {
        UUID evidenceUuid = UUID.fromString(evidenceId);
        Double average = springDataRepository.getAverageScoreByEvidence(evidenceUuid);
        return average != null ? average : 0.0;
    }

    @Override
    public void deleteById(EvidenceValidationId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
