package com.impulse.application.coachingsession.usecase;

import com.impulse.application.coachingsession.dto.CreateCoachingSessionCommand;
import com.impulse.application.coachingsession.dto.CoachingSessionResponse;
import com.impulse.application.coachingsession.mapper.CoachingSessionAppMapper;
import com.impulse.application.coachingsession.port.CoachingSessionRepository;
import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.domain.coachingsession.CoachingSessionDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCoachingSessionUseCase {
    private final CoachingSessionRepository coachingSessionRepository;
    private final CoachingSessionAppMapper coachingSessionMapper;
    public CreateCoachingSessionUseCase(CoachingSessionRepository coachingSessionRepository, CoachingSessionAppMapper coachingSessionMapper) {
        this.coachingSessionRepository = coachingSessionRepository;
        this.coachingSessionMapper = coachingSessionMapper;
    }
    public CoachingSessionResponse execute(CreateCoachingSessionCommand command) {
        validateCommand(command);
        CoachingSession coachingSession = coachingSessionMapper.toDomain(command);
        CoachingSession saved = coachingSessionRepository.save(coachingSession);
        return coachingSessionMapper.toResponse(saved);
    }
    private void validateCommand(CreateCoachingSessionCommand command) {
        if (command == null) throw new CoachingSessionDomainError("CreateCoachingSessionCommand cannot be null");
        if (command.getStudentId() == null || command.getStudentId().trim().isEmpty()) throw new CoachingSessionDomainError("Student ID is required");
        if (command.getCoachId() == null || command.getCoachId().trim().isEmpty()) throw new CoachingSessionDomainError("Coach ID is required");
    }
}
