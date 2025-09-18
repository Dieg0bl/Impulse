package com.impulse.features.challenge.application.port.in;

import com.impulse.features.challenge.application.dto.CreateChallengeCommand;
import com.impulse.features.challenge.application.dto.ChallengeResponse;
import com.impulse.shared.utils.IdempotencyKey;

/**
 * Input Port: CreateChallengePort
 * Defines contract for creating challenges
 */
public interface CreateChallengePort {
    ChallengeResponse execute(CreateChallengeCommand command, IdempotencyKey idempotencyKey);
}
