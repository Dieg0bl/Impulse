package com.impulse.adapters.persistence.challengeparticipation.repository;

import com.impulse.adapters.persistence.challengeparticipation.mapper.ChallengeParticipationJpaMapper;
import com.impulse.application.challengeparticipation.port.ChallengeParticipationRepository;
import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationId;
import com.impulse.domain.enums.ParticipationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChallengeParticipationRepositoryImpl implements ChallengeParticipationRepository {

    private final SpringDataChallengeParticipationRepository springDataRepository;
    private final ChallengeParticipationJpaMapper mapper;

    @Override
    public ChallengeParticipation save(ChallengeParticipation participation) {
        var entity = mapper.toEntity(participation);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ChallengeParticipation> findById(ChallengeParticipationId id) {
        var uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public long countByStatus(ParticipationStatus status) {
        return springDataRepository.findAll().stream()
                .filter(entity -> entity.getStatus() == status)
                .count();
    }
}
