package com.impulse.gamification.controller;

import com.impulse.gamification.StreakFreeze;
import com.impulse.gamification.service.StreakFreezeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/streak-freezes")
public class StreakFreezeController {
    private final StreakFreezeService streakFreezeService;
    public StreakFreezeController(StreakFreezeService streakFreezeService) {
        this.streakFreezeService = streakFreezeService;
    }
    @GetMapping
    public List<StreakFreeze> getAllStreakFreezes() {
        return streakFreezeService.getAllStreakFreezes();
    }
    @GetMapping("/user/{userId}")
    public List<StreakFreeze> getStreakFreezesByUserId(@PathVariable String userId) {
        return streakFreezeService.getStreakFreezesByUserId(userId);
    }
}
