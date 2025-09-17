package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.coachingsession.CoachingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingSessionRepository extends JpaRepository<CoachingSession, Long> {
}


