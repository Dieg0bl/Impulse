package com.impulse.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import com.impulse.common.flags.FlagService;
import java.time.Instant;import java.time.temporal.ChronoUnit;

@Component
public class LifecycleScheduler {
    @Autowired private JdbcTemplate jdbc; @Autowired private FlagService flags;
    // Hourly scan for dormant retos or users to send nudges (guardrails enforced in NotificationService)
    @Scheduled(cron = "0 5 * * * *")
    public void scanDormant(){
        if(!flags.isOn("activation.habit")) return;
        var dormant = jdbc.queryForList("SELECT id FROM retos WHERE updated_at < ? LIMIT 100", Instant.now().minus(2, ChronoUnit.DAYS));
        dormant.forEach(r -> jdbc.update("INSERT INTO notifications(user_id,type,payload) VALUES (?,?,?)", r.get("id"), "HABIT_NUDGE", "{}"));
    }
}
