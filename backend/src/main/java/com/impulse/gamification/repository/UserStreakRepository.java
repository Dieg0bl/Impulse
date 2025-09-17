package com.impulse.gamification.repository;

import com.impulse.gamification.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {
}
