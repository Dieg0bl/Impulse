package com.impulse.gamification.service;

import com.impulse.gamification.Mission;
import com.impulse.gamification.repository.MissionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionService {
    private final MissionRepository missionRepository;
    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }
}
