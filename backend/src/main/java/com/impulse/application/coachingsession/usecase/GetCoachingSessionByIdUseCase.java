package com.impulse.application.coachingsession.usecase;

import com.impulse.application.coachingsession.dto.CoachingSessionResponse;
import com.impulse.application.coachingsession.mapper.CoachingSessionAppMapper;
import com.impulse.application.coachingsession.port.CoachingSessionRepository;
import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.domain.coachingsession.CoachingSessionId;
import com.impulse.domain.coachingsession.CoachingSessionDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCoachingSessionByIdUseCase {
    private final CoachingSessionRepository coachingSessionRepository;
    private final CoachingSessionAppMapper coachingSessionMapper;
    public GetCoachingSessionByIdUseCase(CoachingSessionRepository coachingSessionRepository, CoachingSessionAppMapper coachingSessionMapper) {
        this.coachingSessionRepository = coachingSessionRepository;
        this.coachingSessionMapper = coachingSessionMapper;
    }
    public CoachingSessionResponse execute(String coachingSessionId) {
        if (coachingSessionId == null || coachingSessionId.trim().isEmpty()) throw new CoachingSessionDomainError("CoachingSession ID cannot be null or empty");
        CoachingSessionId id = CoachingSessionId.of(coachingSessionId);
        CoachingSession coachingSession = coachingSessionRepository.findById(id)
            .orElseThrow(() -> new CoachingSessionDomainError("CoachingSession not found with ID: " + coachingSessionId));
        return coachingSessionMapper.toResponse(coachingSession);
    }
}
