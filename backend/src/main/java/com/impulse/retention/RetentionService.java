package com.impulse.retention;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Map;
import com.impulse.analytics.EventTracker;

@Service
public class RetentionService {
    private final JdbcTemplate jdbc; private final EventTracker tracker;
    private static final int[] MILESTONES = {3,7,14,30,60,90};
    public RetentionService(JdbcTemplate jdbc, EventTracker tracker){ this.jdbc=jdbc; this.tracker=tracker; }

    @Transactional
    public void recordActivity(Long userId){
        LocalDate today = LocalDate.now();
        var rows = jdbc.queryForList("SELECT * FROM user_streaks WHERE user_id=?", userId);
        if(rows.isEmpty()){
            jdbc.update("INSERT INTO user_streaks(user_id,current_streak,longest_streak,last_activity_date) VALUES (?,?,?,?)", userId,1,1,today);
            emit(userId,1,1,true);
            return;
        }
        var r = rows.get(0);
        LocalDate last = r.get("last_activity_date")==null? null : LocalDate.parse(r.get("last_activity_date").toString());
        int current = ((Number)r.get("current_streak")).intValue();
        int longest = ((Number)r.get("longest_streak")).intValue();
    if(last != null && last.equals(today)) return; // already counted today
        if(last != null && last.plusDays(1).equals(today)){
            current += 1;
        } else {
            current = 1; // reset
        }
        if(current > longest) longest = current;
    jdbc.update("UPDATE user_streaks SET current_streak=?, longest_streak=?, last_activity_date=? WHERE user_id=?", current, longest, today, userId);
    emit(userId,current,longest,false);
    }

    public Map<String,Object> getStreak(Long userId){
        var list = jdbc.queryForList("SELECT current_streak,longest_streak,last_activity_date FROM user_streaks WHERE user_id=?", userId);
        if(list.isEmpty()) return Map.of("current_streak",0,"longest_streak",0);
        return list.get(0);
    }

    private void emit(Long userId, int current, int longest, boolean first){
        try { tracker.track(userId, "retention_activity", Map.of("current_streak", current, "longest_streak", longest, "first", first), null, "system"); } catch (Exception ignore) {}
        for(int m: MILESTONES){
            if(current==m){
                try { tracker.track(userId, "retention_streak_milestone", Map.of("milestone", m), null, "system"); } catch (Exception ignore) {}
            }
        }
    }
}
