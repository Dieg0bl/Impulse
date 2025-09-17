package com.impulse.adapters.persistence.gamification;

import com.impulse.application.gamification.ports.MissionRepository;
import com.impulse.domain.gamification.Mission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepository {

    private final SpringDataMissionRepository springDataRepository;
    private final MissionJpaMapper mapper;

    @Override
    public Mission save(Mission mission) {
        MissionJpaEntity jpaEntity = mapper.toJpaEntity(mission);
        MissionJpaEntity saved = springDataRepository.save(jpaEntity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Mission> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Mission> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findByIsActiveTrue() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findByType(String type) {
        return springDataRepository.findByType(type)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findByCategory(String category) {
        return springDataRepository.findByCategory(category)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findByDifficulty(String difficulty) {
        return springDataRepository.findByDifficulty(difficulty)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findActiveMissionsInDateRange(LocalDateTime now) {
        return springDataRepository.findActiveMissionsInDateRange(now)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mission> findByCategoryAndDifficulty(String category, String difficulty) {
        return springDataRepository.findByCategoryAndDifficulty(category, difficulty)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id.toString());
    }
}
