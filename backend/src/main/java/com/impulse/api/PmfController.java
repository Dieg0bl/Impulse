package com.impulse.api;

import com.impulse.application.AnalyticsService;
import com.impulse.application.SurveyService;
import com.impulse.application.TimeToValueService;
import com.impulse.common.flags.FlagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/pmf")
public class PmfController {
    private final SurveyService surveys;
    private final AnalyticsService analytics;
    private final TimeToValueService t2v;
    private final FlagService flags;

    public PmfController(SurveyService surveys, AnalyticsService analytics, TimeToValueService t2v, FlagService flags){
        this.surveys = surveys; this.analytics = analytics; this.t2v = t2v; this.flags = flags;
    }

    @PostMapping("/survey/{type}")
    public ResponseEntity<Void> submit(@PathVariable String type, @RequestBody Map<String, Object> answers, Principal p) {
        if (!isFlagOn("pmf.surveys")) return ResponseEntity.notFound().build();
        Long userId = p != null ? parsePrincipal(p) : 1L;
        surveys.submit(userId, type, answers);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signals")
    public Map<String, Object> signals(Principal p) {
        var s = surveys.computeSignals();
    Double nps = s.npsAvg();
    double safeNps = nps == null ? 0d : nps.doubleValue();
    long userId = p == null ? 1L : parsePrincipal(p);
        return Map.of(
            "very_disappointed_pct", s.veryDisappointedPct(),
            "nps_avg", safeNps,
            "top_love_reasons", s.topLoveNgrams(),
            "weekly_aha_users", analytics.weeklyAhaUsers(),
            "t2v_seconds_user", t2v.t2vForUser(userId).toSeconds()
        );
    }

    private boolean isFlagOn(String key) { return flags.isOn(key); }

    private Long parsePrincipal(Principal p) {
    try { return Long.valueOf(p.getName()); } catch (NumberFormatException e) { return 1L; }
    }
}
