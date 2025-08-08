package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class TransparencyService {
    private final JdbcTemplate jdbc;
    public TransparencyService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void publishAmar(LocalDate start, LocalDate end, long recipients, String methodology){
        jdbc.update("INSERT INTO amar_metrics(period_start,period_end,eu_monthly_active_recipients,methodology,published_at) VALUES (?,?,?,?,NOW())",
                start, end, recipients, methodology);
    }

    public Map<String,Object> latestAmar(){
        return jdbc.query("SELECT period_start, period_end, eu_monthly_active_recipients, methodology, published_at FROM amar_metrics ORDER BY published_at DESC LIMIT 1",
                rs -> rs.next() ? Map.of(
                        "period_start", rs.getDate(1).toLocalDate(),
                        "period_end", rs.getDate(2).toLocalDate(),
                        "eu_monthly_active_recipients", rs.getLong(3),
                        "methodology", rs.getString(4),
                        "published_at", rs.getTimestamp(5).toInstant()
                ) : Map.of());
    }
}
