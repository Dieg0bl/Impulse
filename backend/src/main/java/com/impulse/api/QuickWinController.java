package com.impulse.api;

import java.util.Map;
import com.impulse.activation.HabitLoopService;
import com.impulse.application.HabitLoopMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.impulse.application.QuickWinService;
import com.impulse.common.flags.FlagService;
import com.impulse.analytics.EventTracker;

/**
 * Phase 2 Activation endpoints.
 */
@RestController
@RequestMapping("/api/activation")
public class QuickWinController {
    private final QuickWinService quickWinService;
    private final FlagService flagService;
    private final HabitLoopService habitLoopService; // streak/stats
    private final HabitLoopMetricsService habitLoopMetricsService; // heuristics
    private final EventTracker tracker;

    public QuickWinController(QuickWinService quickWinService, FlagService flagService, HabitLoopService habitLoopService, HabitLoopMetricsService habitLoopMetricsService, EventTracker tracker) {
        this.quickWinService = quickWinService;
        this.flagService = flagService;
        this.habitLoopService = habitLoopService;
        this.habitLoopMetricsService = habitLoopMetricsService;
        this.tracker = tracker;
    }

    @GetMapping("/quickwin/{userId}")
    public ResponseEntity<Map<String,Object>> quickwin(@PathVariable Long userId) {
    if(!flagService.isOn("activation.quickwin")) {
            return ResponseEntity.notFound().build();
        }
    var result = quickWinService.quickWinStatus(userId);
    tracker.track(userId, "onboarding_quickwin_viewed", java.util.Map.of("has_quickwin", result.get("achieved")), null, "api");
    return ResponseEntity.ok(result);
    }

    @GetMapping("/onboarding/{userId}")
    public ResponseEntity<Map<String,Object>> onboarding(@PathVariable Long userId) {
    if(!flagService.isOn("activation.onboarding")) {
            return ResponseEntity.notFound().build();
        }
        var result = quickWinService.onboardingProgress(userId);
        tracker.track(userId, "onboarding_progress_viewed", java.util.Map.of(
            "ratio", result.get("ratio"),
            "completed", result.get("completed"),
            "total", result.get("total")
        ), null, "api");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/onboarding/{userId}/steps/{stepKey}/start")
    public ResponseEntity<?> start(@PathVariable Long userId, @PathVariable String stepKey){
        if(!flagService.isOn("activation.onboarding")) return ResponseEntity.notFound().build();
        quickWinService.startStep(userId, stepKey);
        tracker.track(userId, "onboarding_step_started", java.util.Map.of("step", stepKey), null, "api");
        return ResponseEntity.ok(java.util.Map.of("step", stepKey, "status", "started"));
    }

    @PostMapping("/onboarding/{userId}/steps/{stepKey}/complete")
    public ResponseEntity<?> complete(@PathVariable Long userId, @PathVariable String stepKey){
        if(!flagService.isOn("activation.onboarding")) return ResponseEntity.notFound().build();
        boolean ok = quickWinService.completeStep(userId, stepKey);
        boolean aha = quickWinService.hasAha(userId);
        tracker.track(userId, "onboarding_step_completed", java.util.Map.of("step", stepKey, "aha", aha), null, "api");
        if(aha){
            tracker.track(userId, "aha_event", java.util.Map.of("source", "onboarding", "step", stepKey), null, "api");
        }
        return ResponseEntity.ok(java.util.Map.of("step", stepKey, "completed", ok, "aha", aha));
    }

    @GetMapping("/habit/{userId}")
    public ResponseEntity<Map<String,Object>> habit(@PathVariable Long userId){
    if(!flagService.isOn("activation.habit")){
            return ResponseEntity.notFound().build();
        }
    var streakStats = habitLoopService.habitStatus(userId);
    var metrics = habitLoopMetricsService.habitMetrics(userId);
    tracker.track(userId, "onboarding_habit_status_viewed", java.util.Map.of(
        "streakDays", streakStats.getOrDefault("streakDays", 0),
        "lastActionHoursAgo", streakStats.getOrDefault("lastActionHoursAgo", -1L),
        "triggerCount", metrics.get("triggerCount"),
        "actionCount", metrics.get("actionCount"),
        "avgActionLatencyHours", metrics.get("avgActionLatencyHours")
    ), null, "api");
    streakStats.putAll(metrics);
    return ResponseEntity.ok(streakStats);
    }
}
