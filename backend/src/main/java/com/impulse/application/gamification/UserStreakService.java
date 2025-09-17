package com.impulse.application.gamification;

import com.impulse.domain.gamification.UserStreak;
import com.impulse.domain.user.UserId;
import com.impulse.application.gamification.ports.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStreakService {
    private final UserStreakRepository userStreakRepository;

    public List<UserStreak> getAllUserStreaks() {
        return userStreakRepository.findAll();
    }

    public List<UserStreak> getUserStreaksByUserId(UserId userId) {
        return userStreakRepository.findByUserId(userId);
    }

    public Optional<UserStreak> getUserStreakByUserIdAndType(UserId userId, String streakType) {
        return userStreakRepository.findByUserIdAndStreakType(userId, streakType);
    }

    public List<UserStreak> getActiveUserStreaks() {
        return userStreakRepository.findByIsActiveTrue();
    }

    public UserStreak saveUserStreak(UserStreak userStreak) {
        return userStreakRepository.save(userStreak);
    }
}
