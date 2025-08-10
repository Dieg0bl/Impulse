package com.impulse.activation;

import org.springframework.jdbc.core.JdbcTemplate;import org.springframework.stereotype.Service;
import java.time.*;import java.util.Map;

@Service
public class HabitLoopService {
    private final JdbcTemplate jdbc;
    public HabitLoopService(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    public Map<String,Object> habitStatus(Long userId){
        ensureRow(userId);
        var row = jdbc.queryForMap("SELECT last_action_at, streak_days FROM habit_metrics WHERE user_id=?", userId);
        Instant last = ((java.sql.Timestamp)row.get("last_action_at")).toInstant();
        long hoursAgo = Duration.between(last, Instant.now()).toHours();
        int streak = ((Number)row.get("streak_days")).intValue();
        return Map.of("streakDays", streak, "lastActionHoursAgo", hoursAgo);
    }

    public void registerAction(Long userId){
        ensureRow(userId);
        var row = jdbc.queryForMap("SELECT last_action_at, streak_days FROM habit_metrics WHERE user_id=?", userId);
        Instant last = ((java.sql.Timestamp)row.get("last_action_at")).toInstant();
        LocalDate lastDay = LocalDate.ofInstant(last, ZoneOffset.UTC);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int streak = ((Number)row.get("streak_days")).intValue();
        if(today.isAfter(lastDay)){
            if(lastDay.plusDays(1).equals(today)) streak += 1; else streak = 1;
        }
        jdbc.update("UPDATE habit_metrics SET last_action_at=?, streak_days=? WHERE user_id=?", Instant.now(), streak, userId);
    }

    private void ensureRow(Long userId){
        jdbc.update("INSERT IGNORE INTO habit_metrics(user_id,last_action_at,streak_days) VALUES (?,?,?)", userId, Instant.now(), 0);
    }
}
