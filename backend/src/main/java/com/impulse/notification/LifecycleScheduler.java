package com.impulse.notification;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.impulse.common.flags.FlagService;
import com.impulse.retention.RetentionService;

@Component("notificationLifecycleScheduler")
public class LifecycleScheduler {

    private final NotificationService notifications;
    private final FlagService flags; private final RetentionService retention;
    private final JdbcTemplate jdbc;

    public LifecycleScheduler(NotificationService notifications, FlagService flags, JdbcTemplate jdbc, RetentionService retention) {
        this.notifications = notifications;
        this.flags = flags;
        this.jdbc = jdbc;
        this.retention = retention;
    }

    // Ejecuta cada hora
    @Scheduled(fixedRate = 60 * 60 * 1000L)
    public void hourly() {
        Object lifecycle = flags.isOn("activation.habit") ? Boolean.TRUE : Boolean.FALSE; // simplified; consolidate flags
        if(Boolean.TRUE.equals(lifecycle)){
            List<Long> users = notifications.findAtRiskUsers();
            for(Long uid : users){
                notifications.trySend(uid, "email", "deadline_warning");
            }
            // Merge dormant scan (previous separate scheduler) every hour
            var dormant = jdbc.queryForList("SELECT id, usuario_id FROM retos WHERE updated_at < DATE_SUB(NOW(), INTERVAL 2 DAY) LIMIT 50");
            dormant.forEach(r -> {
                Object userId = r.getOrDefault("usuario_id", r.get("id"));
                if(userId instanceof Number n){
                    notifications.trySend(n.longValue(), "email", "habit_nudge");
                }
            });
        }
    }

    // Revisión diaria de rachas rotas: ejecuta a las 07:05 local (approx) (cron: m h * * *)
    @Scheduled(cron = "5 7 * * * *")
    public void brokenStreaks() {
        if(!flags.isOn("retention.streaks")) return;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // Usuarios que tenían last_activity_date = anteayer y no actividad ayer (streak rota)
        var list = jdbc.queryForList("SELECT user_id,current_streak,last_activity_date FROM user_streaks WHERE last_activity_date < ?", yesterday);
        for(var row : list){
            Object uidObj = row.get("user_id");
            if(uidObj instanceof Number n){
                // Se rompe racha: mandar nudge si guardrails permiten
                notifications.trySend(n.longValue(), "email", "streak_broken");
            }
        }
    }
}
