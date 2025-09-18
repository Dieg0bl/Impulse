package com.impulse.features.challenge.application.port.in;

import com.impulse.features.challenge.application.dto.OpenChallengeCommand;
import com.impulse.features.challenge.application.dto.ChallengeResponse;
import com.impulse.shared.utils.IdempotencyKey;

/**
 * Input Port: OpenChallengePort
 * Defines contract for opening challenges
 */
public interface OpenChallengePort {
    ChallengeResponse execute(OpenChallengeCommand command, IdempotencyKey idempotencyKey);
}
