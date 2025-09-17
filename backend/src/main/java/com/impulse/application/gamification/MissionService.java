package com.impulse.application.gamification;

import com.impulse.domain.gamification.Mission;
import com.impulse.application.gamification.ports.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public List<Mission> getActiveMissions() {
        return missionRepository.findByIsActiveTrue();
    }

    public Optional<Mission> getMissionById(UUID id) {
        return missionRepository.findById(id);
    }

    public List<Mission> getMissionsByType(String type) {
        return missionRepository.findByType(type);
    }

    public List<Mission> getMissionsByCategory(String category) {
        return missionRepository.findByCategory(category);
    }

    public Mission saveMission(Mission mission) {
        return missionRepository.save(mission);
    }
}
