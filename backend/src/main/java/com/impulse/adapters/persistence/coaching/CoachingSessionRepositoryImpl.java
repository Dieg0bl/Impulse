package com.impulse.adapters.persistence.coaching;

import com.impulse.adapters.persistence.coaching.mapper.CoachingSessionJpaMapper;
import com.impulse.adapters.persistence.coaching.repository.SpringDataCoachingSessionRepository;
import com.impulse.application.coachingsession.port.CoachingSessionRepository;
import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.domain.coachingsession.CoachingSessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CoachingSessionRepositoryImpl implements CoachingSessionRepository {

    private final SpringDataCoachingSessionRepository springDataRepository;
    private final CoachingSessionJpaMapper mapper;

    @Override
    public CoachingSession save(CoachingSession coachingSession) {
        var entity = mapper.toEntity(coachingSession);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CoachingSession> findById(CoachingSessionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        return springDataRepository.findById(uuid)
                .map(mapper::toDomain);
    }

    @Override
    public List<CoachingSession> findByUserId(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return springDataRepository.findByUserIdOrderByScheduledTimeDesc(userUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingSession> findByCoachId(String coachId) {
        UUID coachUuid = UUID.fromString(coachId);
        return springDataRepository.findByCoachIdOrderByScheduledTimeDesc(coachUuid)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoachingSession> findUpcomingByCoach(String coachId) {
        UUID coachUuid = UUID.fromString(coachId);
        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findUpcomingSessionsByCoach(coachUuid, now)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CoachingSessionId id) {
        UUID uuid = UUID.fromString(id.getValue());
        springDataRepository.deleteById(uuid);
    }
}
