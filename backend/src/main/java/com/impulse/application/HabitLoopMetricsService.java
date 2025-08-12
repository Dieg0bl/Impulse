package com.impulse.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Phase 3 Habit Loop heuristics.
 * Provides simple trigger->action->reward cycle metrics using notification_log and retos tables.
 */
@Service
public class HabitLoopMetricsService {
    private final JdbcTemplate jdbc;

    public HabitLoopMetricsService(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
     * Returns basic habit metrics for a user.
     * triggerCount: notifications sent last 7 days
     * actionCount: retos COMPLETADO last 7 days
     * avgActionLatencyHours: avg time between notification and next completed reto (rough)
     */
    public Map<String,Object> habitMetrics(Long userId){
        Map<String,Object> out = new HashMap<>();
        Integer triggers = jdbc.queryForObject("SELECT COUNT(*) FROM notification_log WHERE user_id=? AND sent_at>=NOW()-INTERVAL 7 DAY", Integer.class, userId);
        Integer actions = jdbc.queryForObject("SELECT COUNT(*) FROM retos WHERE usuario_id=? AND estado='COMPLETADO' AND updated_at>=NOW()-INTERVAL 7 DAY", Integer.class, userId);
        out.put("triggerCount", triggers==null?0:triggers);
        out.put("actionCount", actions==null?0:actions);
        // rough latency: difference between oldest notification in window and newest completed reto
        LocalDateTime oldestNotif = jdbc.query("SELECT sent_at FROM notification_log WHERE user_id=? AND sent_at>=NOW()-INTERVAL 7 DAY ORDER BY sent_at ASC LIMIT 1", rs-> rs.next()? rs.getTimestamp(1).toLocalDateTime(): null, userId);
        LocalDateTime newestAction = jdbc.query("SELECT updated_at FROM retos WHERE usuario_id=? AND estado='COMPLETADO' AND updated_at>=NOW()-INTERVAL 7 DAY ORDER BY updated_at DESC LIMIT 1", rs-> rs.next()? rs.getTimestamp(1).toLocalDateTime(): null, userId);
        if(oldestNotif!=null && newestAction!=null && newestAction.isAfter(oldestNotif)){
            out.put("avgActionLatencyHours", (double) Duration.between(oldestNotif, newestAction).toHours());
        } else {
            out.put("avgActionLatencyHours", null);
        }
        return out;
    }
}
