package com.impulse.features.evidencereview.application.port.in;

import com.impulse.features.evidencereview.application.dto.DecideEvidenceCommand;
import com.impulse.features.evidencereview.application.dto.EvidenceResponse;
import com.impulse.shared.utils.IdempotencyKey;

/**
 * Input Port: DecideEvidencePort
 * Defines contract for approving/rejecting evidence
 */
public interface DecideEvidencePort {
    EvidenceResponse execute(DecideEvidenceCommand command, IdempotencyKey idempotencyKey);
}
