package com.impulse.metrics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductMetricsService {
    private final JdbcTemplate jdbc;
    public ProductMetricsService(JdbcTemplate jdbc){ this.jdbc=jdbc; }

    @Transactional
    public void aggregateDaily(LocalDate day){
        // DAU: usuarios con eventos ese día
    int dau = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE DATE(timestamp)=?", Integer.class, day));
        // WAU/MAU windows
        LocalDate wFrom = day.minusDays(6), mFrom = day.minusDays(29);
    int wau = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE timestamp BETWEEN ? AND ?", Integer.class, wFrom.atStartOfDay().toInstant(ZoneOffset.UTC), day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
    int mau = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE timestamp BETWEEN ? AND ?", Integer.class, mFrom.atStartOfDay().toInstant(ZoneOffset.UTC), day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
    int newUsers = safeInt(jdbc.queryForObject("SELECT COUNT(*) FROM usuarios WHERE DATE(created_at)=?", Integer.class, day));
    int ahaUsers = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE event_type='aha_event' AND DATE(timestamp)=?", Integer.class, day));
        // Retention D1/D7/D30: cohorte newUsers day-X presente hoy
        Double d1 = retention(day,1);
        Double d7 = retention(day,7);
        Double d30 = retention(day,30);
        jdbc.update("REPLACE INTO daily_metrics(metric_date,dau,wau,mau,d1_retention,d7_retention,d30_retention,new_users,aha_users) VALUES (?,?,?,?,?,?,?,?,?)",
            day,dau,wau,mau,d1,d7,d30,newUsers,ahaUsers);
    }

    private Double retention(LocalDate today, int delta){
        LocalDate cohort = today.minusDays(delta);
        Integer cohortSize = jdbc.queryForObject("SELECT COUNT(*) FROM cohort_users WHERE cohort_date=?", Integer.class, cohort);
        if(cohortSize==null || cohortSize==0) return null;
        Integer active = jdbc.queryForObject("SELECT COUNT(DISTINCT e.user_id) FROM events e JOIN cohort_users c ON e.user_id=c.user_id WHERE c.cohort_date=? AND DATE(e.timestamp)=?", Integer.class, cohort, today);
        if(active==null) active=0;
        return (double)active * 100.0 / cohortSize;
    }

    public Map<String,Object> daily(LocalDate day){
        var list = jdbc.queryForList("SELECT * FROM daily_metrics WHERE metric_date=?", day);
        return list.isEmpty()? Map.of() : list.get(0);
    }

    private int safeInt(Integer v){ return v==null?0:v; }

    public List<Map<String,Object>> range(LocalDate from, LocalDate to){
        return jdbc.queryForList("SELECT * FROM daily_metrics WHERE metric_date BETWEEN ? AND ? ORDER BY metric_date", from, to);
    }

    public String rangeCsv(LocalDate from, LocalDate to){
        var rows = range(from,to);
        if(rows.isEmpty()) return "metric_date,dau,wau,mau,d1_retention,d7_retention,d30_retention,new_users,aha_users"; 
        String header = String.join(",", rows.get(0).keySet());
        return header + "\n" + rows.stream().map(r -> r.values().stream().map(v -> v==null?"":v.toString()).collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
    }

    public Map<String,Object> funnel(LocalDate from, LocalDate to){
        // new users
        int newUsers = safeInt(jdbc.queryForObject("SELECT COUNT(*) FROM usuarios WHERE DATE(created_at) BETWEEN ? AND ?", Integer.class, from, to));
        int creators = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT usuario_id) FROM retos WHERE DATE(created_at) BETWEEN ? AND ?", Integer.class, from, to));
        int completers = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT usuario_id) FROM retos WHERE estado='COMPLETADO' AND DATE(updated_at) BETWEEN ? AND ?", Integer.class, from, to));
        int ahaUsers = safeInt(jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE event_type='aha_event' AND DATE(timestamp) BETWEEN ? AND ?", Integer.class, from, to));
        return Map.of(
            "from", from,
            "to", to,
            "new_users", newUsers,
            "reto_creators", creators,
            "reto_completers", completers,
            "aha_users", ahaUsers
        );
    }
}
