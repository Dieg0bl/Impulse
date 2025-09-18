package com.impulse.features.auth.application.port.out;

import com.impulse.features.auth.domain.EmailVerification;
import com.impulse.features.auth.domain.EmailVerificationId;
import java.util.Optional;

/**
 * Outbound port for email verification persistence
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification emailVerification);
    Optional<EmailVerification> findById(EmailVerificationId id);
    Optional<EmailVerification> findByTokenHash(String tokenHash);
    Optional<EmailVerification> findByUserIdAndEmail(Long userId, String email);
    void delete(EmailVerification emailVerification);
    void deleteExpiredTokens();
}
