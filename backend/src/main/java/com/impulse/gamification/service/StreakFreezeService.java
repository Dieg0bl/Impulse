package com.impulse.gamification.service;

import com.impulse.gamification.StreakFreeze;
import com.impulse.gamification.repository.StreakFreezeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StreakFreezeService {
    private final StreakFreezeRepository streakFreezeRepository;
    public StreakFreezeService(StreakFreezeRepository streakFreezeRepository) {
        this.streakFreezeRepository = streakFreezeRepository;
    }
    public List<StreakFreeze> getAllStreakFreezes() {
        return streakFreezeRepository.findAll();
    }
    public List<StreakFreeze> getStreakFreezesByUserId(String userId) {
        return streakFreezeRepository.findAll().stream().filter(sf -> sf.getUserId().equals(userId)).toList();
    }
}
