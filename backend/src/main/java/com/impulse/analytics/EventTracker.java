package com.impulse.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.pmf.Event;
import com.impulse.domain.pmf.EventRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class EventTracker {
    private final EventRepositoryPort repo;
    private final ObjectMapper om = new ObjectMapper();

    public EventTracker(EventRepositoryPort repo){
        this.repo = repo;
    }

    @Transactional
    public void track(Long userId, String type, Map<String, Object> properties, String sessionId, String source) {
        Map<String,Object> safeProps = properties == null ? Map.of() : properties;
        try {
            Event e = new Event();
            e.setUserId(userId);
            e.setEventType(type);
            e.setProperties(om.writeValueAsString(safeProps));
            e.setTimestamp(Instant.now());
            e.setSessionId(sessionId);
            e.setSource(source);
            repo.save(e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
            throw new IllegalArgumentException("Invalid event properties JSON", jpe);
        }
    }
}
