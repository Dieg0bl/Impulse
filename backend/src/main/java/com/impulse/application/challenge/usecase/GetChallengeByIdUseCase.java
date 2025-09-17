package com.impulse.application.challenge.usecase;

import com.impulse.application.challenge.dto.ChallengeResponse;
import com.impulse.application.challenge.mapper.ChallengeAppMapper;
import com.impulse.application.challenge.ports.ChallengeRepository;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.challenge.ChallengeDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetChallengeByIdUseCase - Caso de uso para obtener un challenge por ID
 */
@Service
@Transactional(readOnly = true)
public class GetChallengeByIdUseCase {

    private final ChallengeRepository challengeRepository;
    private final ChallengeAppMapper challengeMapper;

    public GetChallengeByIdUseCase(ChallengeRepository challengeRepository,
                                  ChallengeAppMapper challengeMapper) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
    }

    /**
     * Ejecuta el caso de uso para obtener un challenge por ID
     */
    public ChallengeResponse execute(Long challengeId) {
        if (challengeId == null) {
            throw new ChallengeDomainError("Challenge ID cannot be null");
        }

        ChallengeId id = ChallengeId.of(challengeId);
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeDomainError("Challenge not found with ID: " + challengeId));

        return challengeMapper.toResponse(challenge);
    }
}
