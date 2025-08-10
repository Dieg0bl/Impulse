package com.impulse.notification;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/notifications/prefs")
public class NotificationPrefsController {

    private final JdbcTemplate jdbc;

    public NotificationPrefsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PermitAll
    @GetMapping("/{userId}")
    public Map<String,Object> get(@PathVariable Long userId){
        return jdbc.queryForMap("SELECT * FROM notification_prefs WHERE user_id=?", userId);
    }

    @PermitAll
    @PutMapping("/{userId}")
    public Map<String,Object> update(@PathVariable Long userId, @RequestBody Map<String,Object> body){
        jdbc.update("UPDATE notification_prefs SET allow_email=?, allow_push=?, max_per_day=?, max_per_week=?, quiet_hours_start=?, quiet_hours_end=?, tz=?, updated_at=NOW() WHERE user_id=?",
            body.getOrDefault("allow_email", true),
            body.getOrDefault("allow_push", true),
            body.getOrDefault("max_per_day", 2),
            body.getOrDefault("max_per_week", 6),
            body.getOrDefault("quiet_hours_start", "20:00:00"),
            body.getOrDefault("quiet_hours_end", "09:00:00"),
            body.getOrDefault("tz", "Europe/Madrid"),
            userId);
        return get(userId);
    }

    @PermitAll
    @PostMapping("/{userId}")
    public Map<String,Object> create(@PathVariable Long userId){
        jdbc.update("INSERT IGNORE INTO notification_prefs(user_id) VALUES (?)", userId);
        return get(userId);
    }
}
