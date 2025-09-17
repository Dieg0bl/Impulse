package com.impulse.gamification.controller;

import com.impulse.gamification.Mission;
import com.impulse.gamification.service.MissionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/missions")
public class MissionController {
    private final MissionService missionService;
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }
    @GetMapping
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }
}
