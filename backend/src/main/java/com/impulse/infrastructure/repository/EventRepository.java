package com.impulse.infrastructure.repository;

import com.impulse.domain.pmf.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserIdAndEventType(Long userId, String eventType);

    @Query("SELECT e FROM Event e WHERE e.eventType = :type AND e.timestamp BETWEEN :from AND :to")
    List<Event> findRangeByType(String type, Instant from, Instant to);

    @Query("SELECT e FROM Event e WHERE e.userId = :uid AND e.eventType IN (:types)")
    List<Event> findUserEventsIn(Long uid, List<String> types);
}
