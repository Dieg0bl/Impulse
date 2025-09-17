package com.impulse.gamification.service;

import com.impulse.gamification.UserStreak;
import com.impulse.gamification.repository.UserStreakRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserStreakService {
    private final UserStreakRepository userStreakRepository;
    public UserStreakService(UserStreakRepository userStreakRepository) {
        this.userStreakRepository = userStreakRepository;
    }
    public List<UserStreak> getAllUserStreaks() {
        return userStreakRepository.findAll();
    }
    public List<UserStreak> getUserStreaksByUserId(String userId) {
        return userStreakRepository.findAll().stream().filter(us -> us.getUserId().equals(userId)).toList();
    }
}
