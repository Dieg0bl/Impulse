package com.impulse.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.pmf.Event;
import com.impulse.domain.pmf.EventRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class TimeToValueService {
    private final EventRepositoryPort events;
    public TimeToValueService(EventRepositoryPort events){ this.events = events; }
    private final ObjectMapper om = new ObjectMapper();
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TimeToValueService.class);

    public Duration t2vForUser(Long userId) {
        List<Event> list = events.findUserEventsIn(userId, List.of("challenge_created", "validator_invited", "evidence_uploaded"));
        if (list.isEmpty()) return Duration.ZERO;

        var firstChallenge = new HashMap<String, Instant>();
        var firstInvite = new HashMap<String, Instant>();
        var firstEvidence = new HashMap<String, Instant>();

        for (Event e : list) {
            handleEventForChallengeMaps(e, firstChallenge, firstInvite, firstEvidence);
        }

        return computeBestDuration(firstChallenge, firstInvite, firstEvidence);
    }

    private void handleEventForChallengeMaps(Event e, Map<String, Instant> firstChallenge, Map<String, Instant> firstInvite, Map<String, Instant> firstEvidence) {
        try {
            JsonNode p = om.readTree(e.getProperties() == null ? "{}" : e.getProperties());
            String chal = p.path("challenge_id").asText(null);
            if (chal == null) return;
            switch (e.getEventType()) {
                case "challenge_created" -> firstChallenge.putIfAbsent(chal, e.getTimestamp());
                case "validator_invited" -> firstInvite.putIfAbsent(chal, e.getTimestamp());
                case "evidence_uploaded" -> firstEvidence.putIfAbsent(chal, e.getTimestamp());
                default -> {
                    // intentionally ignore other event types for TTV calculation
                }
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
            // ignore malformed properties but log at debug to aid troubleshooting
            log.debug("Skipping event with invalid properties JSON for event id {}: {}", e.getId(), jpe.getMessage());
        }
    }

    private Duration computeBestDuration(Map<String, Instant> firstChallenge, Map<String, Instant> firstInvite, Map<String, Instant> firstEvidence) {
        Duration best = Duration.ZERO;
        for (var entry : firstChallenge.entrySet()) {
            String chal = entry.getKey();
            Instant t0 = entry.getValue();
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
