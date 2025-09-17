package com.impulse.adapters.persistence.validatorassignment;

import com.impulse.adapters.persistence.validatorassignment.mapper.ValidatorAssignmentJpaMapper;
import com.impulse.adapters.persistence.validatorassignment.repository.SpringDataValidatorAssignmentRepository;
import com.impulse.application.validatorassignment.port.ValidatorAssignmentRepository;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import com.impulse.domain.validatorassignment.ValidatorAssignmentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ValidatorAssignmentRepositoryImpl implements ValidatorAssignmentRepository {

    private final SpringDataValidatorAssignmentRepository springDataRepository;
    private final ValidatorAssignmentJpaMapper mapper;

    @Override
    public ValidatorAssignment save(ValidatorAssignment validatorAssignment) {
        var entity = mapper.toEntity(validatorAssignment);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ValidatorAssignment> findById(ValidatorAssignmentId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<ValidatorAssignment> findByValidatorId(String validatorId) {
        UUID validatorUuid = UUID.fromString(validatorId);
        return springDataRepository.findByValidatorIdOrderByAssignedDateDesc(validatorUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ValidatorAssignment> findByEvidenceId(String evidenceId) {
        UUID evidenceUuid = UUID.fromString(evidenceId);
        return springDataRepository.findByEvidenceIdOrderByAssignedDateDesc(evidenceUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ValidatorAssignment> findByStatus(String status) {
        return springDataRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    // Métodos adicionales útiles
    public List<ValidatorAssignment> findOverdueAssignments() {
        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findOverdueAssignments(now)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public long countCompletedAssignmentsByValidator(String validatorId) {
        UUID validatorUuid = UUID.fromString(validatorId);
        return springDataRepository.countCompletedAssignmentsByValidator(validatorUuid);
    }

    public void deleteById(ValidatorAssignmentId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
