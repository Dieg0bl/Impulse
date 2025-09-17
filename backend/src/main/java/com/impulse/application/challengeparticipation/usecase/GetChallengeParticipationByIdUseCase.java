package com.impulse.application.challengeparticipation.usecase;

import com.impulse.application.challengeparticipation.dto.ChallengeParticipationResponse;
import com.impulse.application.challengeparticipation.mapper.ChallengeParticipationAppMapper;
import com.impulse.application.challengeparticipation.port.ChallengeParticipationRepository;
import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationId;
import com.impulse.domain.challengeparticipation.ChallengeParticipationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetChallengeParticipationByIdUseCase {
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final ChallengeParticipationAppMapper challengeParticipationMapper;
    public GetChallengeParticipationByIdUseCase(ChallengeParticipationRepository challengeParticipationRepository, ChallengeParticipationAppMapper challengeParticipationMapper) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.challengeParticipationMapper = challengeParticipationMapper;
    }
    public ChallengeParticipationResponse execute(Long participationId) {
        if (participationId == null) throw new ChallengeParticipationDomainError("ChallengeParticipation ID cannot be null");
        ChallengeParticipationId id = ChallengeParticipationId.of(participationId);
        ChallengeParticipation participation = challengeParticipationRepository.findById(id)
            .orElseThrow(() -> new ChallengeParticipationDomainError("ChallengeParticipation not found with ID: " + participationId));
        return challengeParticipationMapper.toResponse(participation);
    }
}
