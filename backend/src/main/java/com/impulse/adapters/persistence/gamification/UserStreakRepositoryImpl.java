package com.impulse.adapters.persistence.gamification;

import com.impulse.application.gamification.ports.UserStreakRepository;
import com.impulse.domain.gamification.UserStreak;
import com.impulse.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserStreakRepositoryImpl implements UserStreakRepository {

    private final SpringDataUserStreakRepository springDataRepository;
    private final UserStreakJpaMapper mapper;

    @Override
    public UserStreak save(UserStreak userStreak) {
        UserStreakJpaEntity jpaEntity = mapper.toJpaEntity(userStreak);
        UserStreakJpaEntity saved = springDataRepository.save(jpaEntity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<UserStreak> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<UserStreak> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStreak> findByUserId(UserId userId) {
        return springDataRepository.findByUserId(userId.getValue().toString())
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserStreak> findByUserIdAndStreakType(UserId userId, String streakType) {
        return springDataRepository.findByUserIdAndStreakType(userId.getValue().toString(), streakType)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<UserStreak> findByIsActiveTrue() {
        return springDataRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStreak> findByStreakType(String streakType) {
        return springDataRepository.findByStreakType(streakType)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStreak> findByCurrentStreakGreaterThanEqual(int minStreak) {
        return springDataRepository.findByCurrentStreakGreaterThanEqual(minStreak)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStreak> findByLongestStreakGreaterThanEqual(int minStreak) {
        return springDataRepository.findByLongestStreakGreaterThanEqual(minStreak)
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStreak> findActiveStreaksByUserId(UserId userId) {
        return springDataRepository.findActiveStreaksByUserId(userId.getValue().toString())
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
