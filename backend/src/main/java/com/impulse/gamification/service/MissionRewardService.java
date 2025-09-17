package com.impulse.gamification.service;

import com.impulse.gamification.MissionReward;
import com.impulse.gamification.repository.MissionRewardRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionRewardService {
    private final MissionRewardRepository missionRewardRepository;
    public MissionRewardService(MissionRewardRepository missionRewardRepository) {
        this.missionRewardRepository = missionRewardRepository;
    }
    public List<MissionReward> getAllMissionRewards() {
        return missionRewardRepository.findAll();
    }
    public List<MissionReward> getRewardsByMissionId(String missionId) {
        return missionRewardRepository.findAll().stream().filter(r -> r.getMissionId().equals(missionId)).toList();
    }
}
