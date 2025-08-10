package com.impulse.application;

import java.time.Instant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NorthStarMetricService {
    private final JdbcTemplate jdbc;
    public NorthStarMetricService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public long weeklyActiveAhaUsers(){
        // Proxy NSM: usuarios que tuvieron evento aha_event en últimos 7 días
        Long v = jdbc.queryForObject("SELECT COUNT(DISTINCT user_id) FROM events WHERE event_type='aha_event' AND timestamp>=?", Long.class, Instant.now().minusSeconds(7*24*3600L));
        return v == null ? 0L : v;
    }
}
