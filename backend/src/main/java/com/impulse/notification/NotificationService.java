package com.impulse.notification;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.impulse.analytics.EventTracker;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JdbcTemplate jdbc;
    private final EventTracker tracker;
    private final Clock clock;
    private final io.micrometer.core.instrument.Counter sentCounter;
    private final io.micrometer.core.instrument.Counter blockedCounter;

    @Autowired
    public NotificationService(JdbcTemplate jdbc, EventTracker tracker){
        this(jdbc, tracker, Clock.systemDefaultZone(), null);
    }

    // Convenience overload for tests providing a deterministic Clock without custom registry
    public NotificationService(JdbcTemplate jdbc, EventTracker tracker, Clock clock){
        this(jdbc, tracker, clock, null);
    }

    public NotificationService(JdbcTemplate jdbc, EventTracker tracker, Clock clock, MeterRegistry registry){
        this.jdbc = jdbc;
        this.tracker = tracker;
        this.clock = clock;
        if(registry!=null){
            this.sentCounter = registry.counter("notifications.sent.total");
            this.blockedCounter = registry.counter("notifications.blocked.total");
        } else {
            io.micrometer.core.instrument.simple.SimpleMeterRegistry simple = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
            this.sentCounter = io.micrometer.core.instrument.Counter.builder("notifications.sent.total").register(simple);
            this.blockedCounter = io.micrometer.core.instrument.Counter.builder("notifications.blocked.total").register(simple);
        }
    }

    public boolean trySend(Long userId, String channel, String type){
        Assert.notNull(userId, "userId required");
    Map<String,Object> pref = fetchOrCreatePrefs(userId);
        if(!isChannelAllowed(pref, channel)) return blocked(userId, channel, type, "channel_disabled");
        if(!withinHours(pref)) return blocked(userId, channel, type, "quiet_hours");
        if(exceedsLimits(userId, pref)) return blocked(userId, channel, type, "limits");
        // (stub) envío real se implementará en fase posterior
        jdbc.update("INSERT INTO notification_log(user_id, channel, type) VALUES (?,?,?)", userId, channel, type);
    sentCounter.increment();
    log.info("notification_sent user={} channel={} type={}", userId, channel, type);
        return true;
    }

    public Map<String,Object> guardrailStatus(Long userId){
        Map<String,Object> pref = fetchOrCreatePrefs(userId);
        boolean hours = withinHours(pref);
        boolean limits = exceedsLimits(userId, pref);
        return Map.of(
            "withinHours", hours,
            "underLimits", !limits,
            "maxPerDay", pref.getOrDefault("max_per_day",2),
            "maxPerWeek", pref.getOrDefault("max_per_week",6)
        );
    }

    private boolean blocked(Long userId, String channel, String type, String reason){
    blockedCounter.increment();
    log.info("notification_blocked_guardrail user={} channel={} type={} reason={}", userId, channel, type, reason);
    try { tracker.track(userId, "notification_blocked_guardrail", Map.of("channel",channel,"type",type,"reason",reason), "", "guardrail"); } catch (Exception ignore) {}
        return false;
    }

    private boolean isChannelAllowed(Map<String,Object> pref, String channel){
        return switch (channel){
            case "email" -> (Boolean) pref.getOrDefault("allow_email", true);
            case "push" -> (Boolean) pref.getOrDefault("allow_push", true);
            default -> true;
        };
    }

    private boolean withinHours(Map<String,Object> pref){
        String tz = (String) pref.getOrDefault("tz", "Europe/Madrid");
        ZoneId zone = ZoneId.of(tz);
    LocalDateTime nowDt = LocalDateTime.now(clock).atZone(clock.getZone()).withZoneSameInstant(zone).toLocalDateTime();
    LocalTime now = nowDt.toLocalTime();
        LocalTime quietStart = ((java.sql.Time)pref.get("quiet_hours_start")).toLocalTime();
        LocalTime quietEnd = ((java.sql.Time)pref.get("quiet_hours_end")).toLocalTime();
        // quiet window spans evening -> morning (start > end)
        if(quietStart.isAfter(quietEnd)){
            if(now.isAfter(quietStart) || now.isBefore(quietEnd)) return false;
        } else { // same-day window
            if(!now.isBefore(quietStart) && !now.isAfter(quietEnd)) return false;
        }
        return true;
    }

    private boolean exceedsLimits(Long userId, Map<String,Object> pref){
    Integer perDayI = jdbc.queryForObject("SELECT COUNT(*) FROM notification_log WHERE user_id=? AND DATE(sent_at)=CURRENT_DATE", Integer.class, userId);
    Integer perWeekI = jdbc.queryForObject("SELECT COUNT(*) FROM notification_log WHERE user_id=? AND YEARWEEK(sent_at,1)=YEARWEEK(NOW(),1)", Integer.class, userId);
    int perDay = perDayI==null?0:perDayI;
    int perWeek = perWeekI==null?0:perWeekI;
        int maxDay = ((Number)pref.getOrDefault("max_per_day",2)).intValue();
        int maxWeek = ((Number)pref.getOrDefault("max_per_week",6)).intValue();
        return perDay >= maxDay || perWeek >= maxWeek;
    }

    public List<Long> findAtRiskUsers(){
        return jdbc.query("SELECT user_id FROM notification_prefs LIMIT 50", (rs,i)-> rs.getLong(1));
    }

    private Map<String,Object> fetchOrCreatePrefs(Long userId){
        try {
            return jdbc.queryForMap("SELECT * FROM notification_prefs WHERE user_id=?", userId);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex){
            jdbc.update("INSERT INTO notification_prefs(user_id) VALUES (?)", userId);
            return jdbc.queryForMap("SELECT * FROM notification_prefs WHERE user_id=?", userId);
        }
    }
}
