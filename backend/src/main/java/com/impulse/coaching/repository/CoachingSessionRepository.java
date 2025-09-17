package com.impulse.coaching.repository;

import com.impulse.domain.model.CoachingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingSessionRepository extends JpaRepository<CoachingSession, Long> {
}
