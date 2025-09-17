package com.impulse.adapters.persistence.coaching;

import com.impulse.adapters.persistence.coaching.mapper.CoachingInteractionJpaMapper;
import com.impulse.adapters.persistence.coaching.repository.SpringDataCoachingInteractionRepository;
import com.impulse.application.coachinginteraction.port.CoachingInteractionRepository;
import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.domain.coachinginteraction.CoachingInteractionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CoachingInteractionRepositoryImpl implements CoachingInteractionRepository {

    private final SpringDataCoachingInteractionRepository springDataRepository;
    private final CoachingInteractionJpaMapper mapper;

    @Override
    public CoachingInteraction save(CoachingInteraction coachingInteraction) {
        var entity = mapper.toEntity(coachingInteraction);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CoachingInteraction> findById(CoachingInteractionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<CoachingInteraction> findBySessionId(String sessionId) {
        UUID sessionUuid = UUID.fromString(sessionId);
        return springDataRepository.findBySessionId(sessionUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingInteraction> findByUserId(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return springDataRepository.findByUserIdOrderByTimestampDesc(userUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingInteraction> findByCoachId(String coachId) {
        UUID coachUuid = UUID.fromString(coachId);
        return springDataRepository.findByCoachIdOrderByTimestampDesc(coachUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CoachingInteractionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
