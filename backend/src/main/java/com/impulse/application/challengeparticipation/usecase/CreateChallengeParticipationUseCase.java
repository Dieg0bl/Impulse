package com.impulse.application.challengeparticipation.usecase;

import com.impulse.application.challengeparticipation.dto.CreateChallengeParticipationCommand;
import com.impulse.application.challengeparticipation.dto.ChallengeParticipationResponse;
import com.impulse.application.challengeparticipation.mapper.ChallengeParticipationAppMapper;
import com.impulse.application.challengeparticipation.port.ChallengeParticipationRepository;
import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateChallengeParticipationUseCase {
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final ChallengeParticipationAppMapper challengeParticipationMapper;
    public CreateChallengeParticipationUseCase(ChallengeParticipationRepository challengeParticipationRepository, ChallengeParticipationAppMapper challengeParticipationMapper) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.challengeParticipationMapper = challengeParticipationMapper;
    }
    public ChallengeParticipationResponse execute(CreateChallengeParticipationCommand command) {
        validateCommand(command);
        ChallengeParticipation participation = challengeParticipationMapper.toDomain(command);
        ChallengeParticipation saved = challengeParticipationRepository.save(participation);
        return challengeParticipationMapper.toResponse(saved);
    }
    private void validateCommand(CreateChallengeParticipationCommand command) {
        if (command == null) throw new ChallengeParticipationDomainError("CreateChallengeParticipationCommand cannot be null");
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) throw new ChallengeParticipationDomainError("UserId is required");
        if (command.getChallengeId() == null) throw new ChallengeParticipationDomainError("ChallengeId is required");
        if (command.getStatus() == null) throw new ChallengeParticipationDomainError("Status is required");
    }
}
