package com.impulse.gamification.controller;

import com.impulse.gamification.UserStreak;
import com.impulse.gamification.service.UserStreakService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification/user-streaks")
public class UserStreakController {
    private final UserStreakService userStreakService;
    public UserStreakController(UserStreakService userStreakService) {
        this.userStreakService = userStreakService;
    }
    @GetMapping
    public List<UserStreak> getAllUserStreaks() {
        return userStreakService.getAllUserStreaks();
    }
    @GetMapping("/user/{userId}")
    public List<UserStreak> getUserStreaksByUserId(@PathVariable String userId) {
        return userStreakService.getUserStreaksByUserId(userId);
    }
}
