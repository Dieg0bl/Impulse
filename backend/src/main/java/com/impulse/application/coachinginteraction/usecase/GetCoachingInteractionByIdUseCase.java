package com.impulse.application.coachinginteraction.usecase;

import com.impulse.application.coachinginteraction.dto.CoachingInteractionResponse;
import com.impulse.application.coachinginteraction.mapper.CoachingInteractionAppMapper;
import com.impulse.application.coachinginteraction.port.CoachingInteractionRepository;
import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.domain.coachinginteraction.CoachingInteractionId;
import com.impulse.domain.coachinginteraction.CoachingInteractionDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCoachingInteractionByIdUseCase {
    private final CoachingInteractionRepository coachingInteractionRepository;
    private final CoachingInteractionAppMapper coachingInteractionMapper;
    public GetCoachingInteractionByIdUseCase(CoachingInteractionRepository coachingInteractionRepository, CoachingInteractionAppMapper coachingInteractionMapper) {
        this.coachingInteractionRepository = coachingInteractionRepository;
        this.coachingInteractionMapper = coachingInteractionMapper;
    }
    public CoachingInteractionResponse execute(String coachingInteractionId) {
        if (coachingInteractionId == null || coachingInteractionId.trim().isEmpty()) throw new CoachingInteractionDomainError("CoachingInteraction ID cannot be null or empty");
        CoachingInteractionId id = CoachingInteractionId.of(coachingInteractionId);
        CoachingInteraction coachingInteraction = coachingInteractionRepository.findById(id)
            .orElseThrow(() -> new CoachingInteractionDomainError("CoachingInteraction not found with ID: " + coachingInteractionId));
        return coachingInteractionMapper.toResponse(coachingInteraction);
    }
}
