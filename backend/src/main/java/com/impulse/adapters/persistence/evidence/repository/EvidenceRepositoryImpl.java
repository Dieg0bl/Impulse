package com.impulse.adapters.persistence.evidence.repository;

import com.impulse.adapters.persistence.evidence.entity.EvidenceJpaEntity;
import com.impulse.adapters.persistence.evidence.mapper.EvidenceJpaMapper;
import com.impulse.application.evidence.port.EvidenceRepository;
import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceId;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.EvidenceStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EvidenceRepositoryImpl - ImplementaciÃ³n JPA del repositorio de Evidence
 */
@Repository
public class EvidenceRepositoryImpl implements EvidenceRepository {

    private final SpringDataEvidenceRepository springDataRepository;
    private final EvidenceJpaMapper evidenceJpaMapper;

    public EvidenceRepositoryImpl(SpringDataEvidenceRepository springDataRepository,
                                 EvidenceJpaMapper evidenceJpaMapper) {
        this.springDataRepository = springDataRepository;
        this.evidenceJpaMapper = evidenceJpaMapper;
    }

    @Override
    public Evidence save(Evidence evidence) {
        EvidenceJpaEntity jpaEntity = evidenceJpaMapper.toJpaEntity(evidence);
        EvidenceJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        return evidenceJpaMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Evidence> findById(EvidenceId evidenceId) {
        Optional<EvidenceJpaEntity> jpaEntity = springDataRepository.findByIdAndNotArchived(evidenceId.asLong());
        return jpaEntity.map(evidenceJpaMapper::toDomainEntity);
    }

    @Override
    public List<Evidence> findByUserId(UserId userId) {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findByUserId(userId.asLong());
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findByChallengeId(ChallengeId challengeId) {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findByChallengeId(challengeId.asLong());
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findByStatus(EvidenceStatus status) {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findByStatus(status);
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findByUserIdAndChallengeId(UserId userId, ChallengeId challengeId) {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findByUserIdAndChallengeId(userId.asLong(), challengeId.asLong());
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findPendingValidation() {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findPendingValidation();
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findAll() {
        List<EvidenceJpaEntity> jpaEntities = springDataRepository.findAll();
        return jpaEntities.stream().map(evidenceJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public void deleteById(EvidenceId evidenceId) {
        Optional<EvidenceJpaEntity> optionalEntity = springDataRepository.findById(evidenceId.asLong());
        if (optionalEntity.isPresent()) {
            EvidenceJpaEntity entity = optionalEntity.get();
            entity.setStatus(EvidenceStatus.ARCHIVED);
            springDataRepository.save(entity);
        }
    }

    @Override
    public boolean existsById(EvidenceId evidenceId) {
        Optional<EvidenceJpaEntity> entity = springDataRepository.findByIdAndNotArchived(evidenceId.asLong());
        return entity.isPresent();
    }

    @Override
    public long count() {
        return springDataRepository.count();
    }

    @Override
    public long countByStatus(EvidenceStatus status) {
        return springDataRepository.countByStatus(status);
    }

    @Override
    public long countByChallengeId(ChallengeId challengeId) {
        return springDataRepository.countByChallengeId(challengeId.asLong());
    }
}


