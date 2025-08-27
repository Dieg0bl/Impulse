package com.impulse.application.ports;

import com.impulse.domain.pmf.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import java.util.List;

    @NoRepositoryBean
    public interface EventPort extends JpaRepository<Event, Long> {
    // Derived query: find events by userId and where eventType is in the provided list
    List<Event> findByUserIdAndEventTypeIn(Long userId, java.util.List<String> eventTypes);

    // Firma genérica compatible con CrudRepository para evitar ambigüedad y preservar null-safety
    @NonNull
    <S extends Event> S save(@NonNull S entity);
}
