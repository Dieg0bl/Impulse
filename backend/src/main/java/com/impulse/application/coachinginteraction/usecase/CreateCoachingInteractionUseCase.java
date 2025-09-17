package com.impulse.application.coachinginteraction.usecase;

import com.impulse.application.coachinginteraction.dto.CreateCoachingInteractionCommand;
import com.impulse.application.coachinginteraction.dto.CoachingInteractionResponse;
import com.impulse.application.coachinginteraction.mapper.CoachingInteractionAppMapper;
import com.impulse.application.coachinginteraction.port.CoachingInteractionRepository;
import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.domain.coachinginteraction.CoachingInteractionDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCoachingInteractionUseCase {
    private final CoachingInteractionRepository coachingInteractionRepository;
    private final CoachingInteractionAppMapper coachingInteractionMapper;
    public CreateCoachingInteractionUseCase(CoachingInteractionRepository coachingInteractionRepository, CoachingInteractionAppMapper coachingInteractionMapper) {
        this.coachingInteractionRepository = coachingInteractionRepository;
        this.coachingInteractionMapper = coachingInteractionMapper;
    }
    public CoachingInteractionResponse execute(CreateCoachingInteractionCommand command) {
        validateCommand(command);
        CoachingInteraction coachingInteraction = coachingInteractionMapper.toDomain(command);
        CoachingInteraction saved = coachingInteractionRepository.save(coachingInteraction);
        return coachingInteractionMapper.toResponse(saved);
    }
    private void validateCommand(CreateCoachingInteractionCommand command) {
        if (command == null) throw new CoachingInteractionDomainError("CreateCoachingInteractionCommand cannot be null");
        if (command.getSessionId() == null || command.getSessionId().trim().isEmpty()) throw new CoachingInteractionDomainError("Session ID is required");
        if (command.getType() == null || command.getType().trim().isEmpty()) throw new CoachingInteractionDomainError("Type is required");
    }
}
