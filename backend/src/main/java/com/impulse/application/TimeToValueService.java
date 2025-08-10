package com.impulse.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.pmf.Event;
import com.impulse.infrastructure.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class TimeToValueService {
    private final EventRepository events;
    public TimeToValueService(EventRepository events){ this.events = events; }
    private final ObjectMapper om = new ObjectMapper();

    public Duration t2vForUser(Long userId) {
        List<Event> list = events.findUserEventsIn(userId, List.of("challenge_created", "validator_invited", "evidence_uploaded"));
        if (list.isEmpty()) return Duration.ZERO;

        Map<String, Instant> firstChallenge = new HashMap<>();
        Map<String, Instant> firstInvite = new HashMap<>();
        Map<String, Instant> firstEvidence = new HashMap<>();

        for (Event e : list) {
            try {
                JsonNode p = om.readTree(e.getProperties() == null ? "{}" : e.getProperties());
                String chal = p.path("challenge_id").asText(null);
                if (chal == null) continue;
                switch (e.getEventType()) {
                    case "challenge_created" -> firstChallenge.putIfAbsent(chal, e.getTimestamp());
                    case "validator_invited" -> firstInvite.putIfAbsent(chal, e.getTimestamp());
                    case "evidence_uploaded" -> firstEvidence.putIfAbsent(chal, e.getTimestamp());
                }
            } catch (Exception ignored) { }
        }

        Duration best = Duration.ZERO;
        for (String chal : firstChallenge.keySet()) {
            Instant t0 = firstChallenge.get(chal);
            Instant t1 = firstInvite.get(chal);
            Instant t2 = firstEvidence.get(chal);
            if (t0 != null && t1 != null && t2 != null && !t0.isAfter(t1) && !t1.isAfter(t2)) {
                Duration d = Duration.between(t0, t2);
                if (best.isZero() || d.compareTo(best) < 0) best = d;
            }
        }
        return best;
    }
}
