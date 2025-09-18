package com.impulse.features.evidencereview.application.port.in;

import com.impulse.features.evidencereview.application.dto.SubmitEvidenceCommand;
import com.impulse.features.evidencereview.application.dto.EvidenceResponse;
import com.impulse.shared.utils.IdempotencyKey;

/**
 * Input Port: SubmitEvidencePort
 * Defines contract for submitting evidence
 */
public interface SubmitEvidencePort {
    EvidenceResponse execute(SubmitEvidenceCommand command, IdempotencyKey idempotencyKey);
}
