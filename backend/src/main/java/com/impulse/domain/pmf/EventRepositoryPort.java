package com.impulse.domain.pmf;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    Optional<Event> findById(Long id);
    Event save(Event event);
    List<Event> findAll();
    void deleteById(Long id);
    List<Event> findByUserIdAndEventType(Long userId, String eventType);
    List<Event> findRangeByType(String type, Instant from, Instant to);
    List<Event> findUserEventsIn(Long uid, List<String> types);
}
