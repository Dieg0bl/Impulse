package com.impulse.adapters.persistence.challenge.repository;

import com.impulse.adapters.persistence.challenge.entity.ChallengeJpaEntity;
import com.impulse.adapters.persistence.challenge.mapper.ChallengeJpaMapper;
import com.impulse.application.challenge.port.ChallengeRepository;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.enums.ChallengeStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChallengeRepositoryImpl - ImplementaciÃ³n JPA del repositorio de Challenge
 */
@Repository
public class ChallengeRepositoryImpl implements ChallengeRepository {

    private final SpringDataChallengeRepository springDataRepository;
    private final ChallengeJpaMapper challengeJpaMapper;

    public ChallengeRepositoryImpl(SpringDataChallengeRepository springDataRepository,
                                  ChallengeJpaMapper challengeJpaMapper) {
        this.springDataRepository = springDataRepository;
        this.challengeJpaMapper = challengeJpaMapper;
    }

    @Override
    public Challenge save(Challenge challenge) {
        ChallengeJpaEntity jpaEntity = challengeJpaMapper.toJpaEntity(challenge);
        ChallengeJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        return challengeJpaMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Challenge> findById(ChallengeId challengeId) {
        Optional<ChallengeJpaEntity> jpaEntity = springDataRepository.findByIdAndNotDeleted(challengeId.asLong());
        return jpaEntity.map(challengeJpaMapper::toDomainEntity);
    }

    @Override
    public List<Challenge> findByStatus(ChallengeStatus status) {
        List<ChallengeJpaEntity> jpaEntities = springDataRepository.findByStatus(status);
        return jpaEntities.stream()
                .map(challengeJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Challenge> findAll() {
        List<ChallengeJpaEntity> jpaEntities = springDataRepository.findAll();
        return jpaEntities.stream()
                .map(challengeJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ChallengeId challengeId) {
        Optional<ChallengeJpaEntity> optionalEntity = springDataRepository.findById(challengeId.asLong());
        if (optionalEntity.isPresent()) {
            ChallengeJpaEntity entity = optionalEntity.get();
            entity.setStatus(ChallengeStatus.DELETED);
            springDataRepository.save(entity);
        }
    }

    @Override
    public boolean existsById(ChallengeId challengeId) {
        Optional<ChallengeJpaEntity> entity = springDataRepository.findByIdAndNotDeleted(challengeId.asLong());
        return entity.isPresent();
    }

    @Override
    public long count() {
        return springDataRepository.count();
    }
}


