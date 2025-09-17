package com.impulse.gamification.controller;

import com.impulse.gamification.MissionObjective;
import com.impulse.gamification.service.MissionObjectiveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/mission-objectives")
public class MissionObjectiveController {
    private final MissionObjectiveService missionObjectiveService;
    public MissionObjectiveController(MissionObjectiveService missionObjectiveService) {
        this.missionObjectiveService = missionObjectiveService;
    }
    @GetMapping
    public List<MissionObjective> getAllMissionObjectives() {
        return missionObjectiveService.getAllMissionObjectives();
    }
    @GetMapping("/mission/{missionId}")
    public List<MissionObjective> getObjectivesByMissionId(@PathVariable String missionId) {
        return missionObjectiveService.getObjectivesByMissionId(missionId);
    }
}
