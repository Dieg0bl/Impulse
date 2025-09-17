package com.impulse.gamification.service;

import com.impulse.gamification.MissionObjective;
import com.impulse.gamification.repository.MissionObjectiveRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionObjectiveService {
    private final MissionObjectiveRepository missionObjectiveRepository;
    public MissionObjectiveService(MissionObjectiveRepository missionObjectiveRepository) {
        this.missionObjectiveRepository = missionObjectiveRepository;
    }
    public List<MissionObjective> getAllMissionObjectives() {
        return missionObjectiveRepository.findAll();
    }
    public List<MissionObjective> getObjectivesByMissionId(String missionId) {
        return missionObjectiveRepository.findAll().stream().filter(obj -> obj.getMissionId().equals(missionId)).toList();
    }
}
