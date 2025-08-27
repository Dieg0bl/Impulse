package com.impulse.domain.repository;

import java.util.Optional;
import java.util.List;
import com.impulse.domain.analytics.Event;

public interface EventRepositoryPort {
    Optional<Event> findById(Long id);
    Event save(Event event);
    List<Event> findAll();
    void deleteById(Long id);
    // Métodos personalizados según uso en AnalyticsService, TimeToValueService, etc.
}
