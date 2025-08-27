package com.impulse.application;

import com.impulse.domain.pmf.EventRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AnalyticsService {
    private final EventRepositoryPort events;
    public AnalyticsService(EventRepositoryPort events){ this.events = events; }

    public long weeklyAhaUsers() {
        var from = Instant.now().minus(7, ChronoUnit.DAYS);
        return events.findRangeByType("aha_event", from, Instant.now())
                .stream().map(e -> e.getUserId()).filter(u -> u != null).distinct().count();
    }
}
