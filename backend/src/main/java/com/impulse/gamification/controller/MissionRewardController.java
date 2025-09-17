package com.impulse.gamification.controller;

import com.impulse.gamification.MissionReward;
import com.impulse.gamification.service.MissionRewardService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/mission-rewards")
public class MissionRewardController {
    private final MissionRewardService missionRewardService;
    public MissionRewardController(MissionRewardService missionRewardService) {
        this.missionRewardService = missionRewardService;
    }
    @GetMapping
    public List<MissionReward> getAllMissionRewards() {
        return missionRewardService.getAllMissionRewards();
    }
    @GetMapping("/mission/{missionId}")
    public List<MissionReward> getRewardsByMissionId(@PathVariable String missionId) {
        return missionRewardService.getRewardsByMissionId(missionId);
    }
}
