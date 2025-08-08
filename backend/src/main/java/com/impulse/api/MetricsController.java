package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.NorthStarMetricService;
import com.impulse.application.EconomicsService;
import com.impulse.application.EconomicsAdvancedService;
import com.impulse.analytics.EventTracker;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final NorthStarMetricService nsm;
    private final EconomicsService economics;
    private final FlagService flags;
    private final EconomicsAdvancedService economicsAdv;
    private final EventTracker tracker;
    public MetricsController(NorthStarMetricService nsm, EconomicsService economics, FlagService flags, EconomicsAdvancedService economicsAdv, EventTracker tracker){this.nsm=nsm;this.economics=economics;this.flags=flags;this.economicsAdv=economicsAdv;this.tracker=tracker;}

    @GetMapping("/nsm/weekly-aha")
    public ResponseEntity<?> weeklyAha(){
    if(!flags.isOn("economics.nsm")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of("weeklyActiveAhaUsers", nsm.weeklyActiveAhaUsers()));
    }

    @PostMapping("/economics/increment/{name}/{delta}")
    public ResponseEntity<?> inc(@PathVariable String name, @PathVariable long delta){
    if(!flags.isOn("economics.counters")) return ResponseEntity.notFound().build();
        economics.increment(name, delta);
        return ResponseEntity.ok(economics.get(name));
    }

    @GetMapping("/economics/{name}")
    public ResponseEntity<?> get(@PathVariable String name){
    if(!flags.isOn("economics.counters")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(economics.get(name));
    }

    @GetMapping("/economics/summary/advanced")
    public ResponseEntity<?> advanced(){
        if(!flags.isOn("economics.advanced")) return ResponseEntity.notFound().build();
        var data = economicsAdv.summary();
        tracker.track(null, "economics_summary_viewed", java.util.Map.of("mrr", data.get("mrr"), "arpu", data.get("arpu")), null, "api");
        return ResponseEntity.ok(data);
    }
}
