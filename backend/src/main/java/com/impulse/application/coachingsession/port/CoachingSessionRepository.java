package com.impulse.application.coachingsession.port;

import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.domain.coachingsession.CoachingSessionId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CoachingSessionRepository {
    CoachingSession save(CoachingSession coachingSession);
    Optional<CoachingSession> findById(CoachingSessionId id);
    List<CoachingSession> findByCoachIdAndDateRange(String coachId, LocalDateTime startDate, LocalDateTime endDate);
    List<CoachingSession> findByStudentId(String studentId);
}
