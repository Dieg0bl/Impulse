package com.impulse.application.gamification.ports;

import com.impulse.domain.gamification.UserStreak;
import com.impulse.domain.user.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStreakRepository {
    UserStreak save(UserStreak userStreak);
    Optional<UserStreak> findById(UUID id);
    List<UserStreak> findAll();
    List<UserStreak> findByUserId(UserId userId);
    Optional<UserStreak> findByUserIdAndStreakType(UserId userId, String streakType);
    List<UserStreak> findByIsActiveTrue();
    List<UserStreak> findByStreakType(String streakType);
    List<UserStreak> findByCurrentStreakGreaterThanEqual(int minStreak);
    List<UserStreak> findByLongestStreakGreaterThanEqual(int minStreak);
    List<UserStreak> findActiveStreaksByUserId(UserId userId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
