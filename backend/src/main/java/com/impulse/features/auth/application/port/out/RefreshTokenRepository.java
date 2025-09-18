package com.impulse.features.auth.application.port.out;

import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.features.auth.domain.RefreshTokenId;
import java.util.Optional;
import java.util.List;

/**
 * Outbound port for refresh token persistence
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findById(RefreshTokenId id);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findActiveByUserId(Long userId);
    void delete(RefreshToken refreshToken);
    void deleteAllByUserId(Long userId);
    void deleteExpiredTokens();
}
