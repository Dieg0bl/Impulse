package com.impulse.application.gamification.ports;

import com.impulse.domain.gamification.Mission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MissionRepository {
    Mission save(Mission mission);
    Optional<Mission> findById(UUID id);
    List<Mission> findAll();
    List<Mission> findByIsActiveTrue();
    List<Mission> findByType(String type);
    List<Mission> findByCategory(String category);
    List<Mission> findByDifficulty(String difficulty);
    List<Mission> findActiveMissionsInDateRange(LocalDateTime now);
    List<Mission> findByCategoryAndDifficulty(String category, String difficulty);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
