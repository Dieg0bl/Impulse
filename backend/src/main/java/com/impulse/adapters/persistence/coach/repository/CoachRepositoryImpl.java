package com.impulse.adapters.persistence.coach.repository;

import com.impulse.adapters.persistence.coach.entity.CoachJpaEntity;
import com.impulse.adapters.persistence.coach.mapper.CoachJpaMapper;
import com.impulse.application.coach.port.CoachRepository;
import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachId;
import com.impulse.domain.enums.CoachStatus;
import com.impulse.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CoachRepositoryImpl implements CoachRepository {

    private final SpringDataCoachRepository springDataCoachRepository;
    private final CoachJpaMapper coachJpaMapper;

    @Override
    public Coach save(Coach coach) {
        CoachJpaEntity entity = coachJpaMapper.toEntity(coach);
        CoachJpaEntity saved = springDataCoachRepository.save(entity);
        return coachJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Coach> findById(CoachId coachId) {
        UUID uuid = UUID.fromString(coachId.getValue());
        return springDataCoachRepository.findById(uuid)
                .map(coachJpaMapper::toDomain);
    }

    @Override
    public Optional<Coach> findByUserId(UserId userId) {
        UUID userUuid = UUID.fromString(userId.getValue());
        return springDataCoachRepository.findByUserId(userUuid)
                .map(coachJpaMapper::toDomain);
    }

    @Override
    public List<Coach> findByStatus(CoachStatus status) {
        return springDataCoachRepository.findByStatus(status)
                .stream()
                .map(coachJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coach> findAll() {
        return springDataCoachRepository.findAll()
                .stream()
                .map(coachJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CoachId coachId) {
        UUID uuid = UUID.fromString(coachId.getValue());
        springDataCoachRepository.deleteById(uuid);
    }

    @Override
    public boolean existsById(CoachId coachId) {
        UUID uuid = UUID.fromString(coachId.getValue());
        return springDataCoachRepository.existsById(uuid);
    }
}


