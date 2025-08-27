package com.impulse.lifecycle;


import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.impulse.analytics.EventTracker;

@Service("lifecycleNotificationService")
public class NotificationService {
    private final JdbcTemplate jdbc;
    private final EventTracker tracker;

    public NotificationService(JdbcTemplate jdbc, EventTracker tracker){
        this.jdbc = jdbc;
        this.tracker = tracker;
    }

    public List<Long> findAtRiskUsers(){
        // Placeholder heurística simple (ajustar a tabla real de usuarios)
        try {
            return jdbc.queryForList("SELECT id FROM usuario LIMIT 25", Long.class);
        } catch(Exception e){
            try { return jdbc.queryForList("SELECT id FROM users LIMIT 25", Long.class); } catch(Exception ex){ return List.of(); }
        }
    }

    public boolean trySend(Long userId, String channel, String type){
        Map<String,Object> pref;
        try { pref = jdbc.queryForMap("SELECT * FROM notification_prefs WHERE user_id=?", userId);} catch(Exception e){return false;}
        String tz = (String) pref.getOrDefault("tz","Europe/Madrid");
        ZoneId zone = ZoneId.of(tz);
        LocalTime now = LocalTime.now(zone);
        LocalTime start = ((java.sql.Time)pref.get("quiet_hours_end")).toLocalTime();
        LocalTime end = ((java.sql.Time)pref.get("quiet_hours_start")).toLocalTime();
        if (now.isBefore(start) || now.isAfter(end)) { logBlocked(userId,channel,type); return false; }
    Integer perDayI = jdbc.queryForObject("SELECT COUNT(*) FROM notification_log WHERE user_id=? AND DATE(sent_at)=CURRENT_DATE", Integer.class, userId);
    Integer perWeekI = jdbc.queryForObject("SELECT COUNT(*) FROM notification_log WHERE user_id=? AND YEARWEEK(sent_at,1)=YEARWEEK(NOW(),1)", Integer.class, userId);
    int perDay = perDayI==null?0:perDayI;
    int perWeek = perWeekI==null?0:perWeekI;
        int maxDay = ((Number)pref.get("max_per_day")).intValue();
        int maxWeek = ((Number)pref.get("max_per_week")).intValue();
        if (perDay>=maxDay || perWeek>=maxWeek) { logBlocked(userId,channel,type); return false; }
        jdbc.update("INSERT INTO notification_log(user_id,channel,type) VALUES (?,?,?)", userId, channel, type);
        return true;
    }

    private void logBlocked(Long userId, String channel, String type){
        try {
            tracker.track(userId, "notification_blocked_guardrail", Map.of("channel",channel,"type",type), "", "guardrail");
        } catch (Exception ignore) {
            // Silenciar errores de tracking para no afectar el flujo principal
        }
    }
}
