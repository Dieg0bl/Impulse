package com.impulse.api;

import com.impulse.analytics.EventTracker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventsController {
    private final EventTracker tracker;
    public EventsController(EventTracker tracker){ this.tracker = tracker; }

    @PostMapping
    public ResponseEntity<Void> track(@RequestBody Map<String, Object> body) {
        String type = String.valueOf(body.get("event_type"));
        Long uid = body.get("user_id") == null ? 1L : Long.valueOf(String.valueOf(body.get("user_id")));
        String session = (String) body.getOrDefault("session_id", "");
        String source = (String) body.getOrDefault("source", "web");
        tracker.track(uid, type, body, session, source);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/now")
    public Map<String, Object> now() { return Map.of("ts", Instant.now().toString()); }
}
