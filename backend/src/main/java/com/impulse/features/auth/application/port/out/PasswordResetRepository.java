package com.impulse.features.auth.application.port.out;

import com.impulse.features.auth.domain.PasswordReset;
import com.impulse.features.auth.domain.PasswordResetId;

import java.util.Optional;

/**
 * PasswordResetRepository - Port de salida para persistencia de password resets
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface PasswordResetRepository {

    /**
     * Guarda un password reset
     */
    PasswordReset save(PasswordReset passwordReset);

    /**
     * Busca password reset por ID
     */
    Optional<PasswordReset> findById(PasswordResetId id);

    /**
     * Busca password reset por token hash
     */
    Optional<PasswordReset> findByTokenHash(String tokenHash);

    /**
     * Busca password reset activo por user ID
     */
    Optional<PasswordReset> findActiveByUserId(Long userId);

    /**
     * Elimina password resets expirados
     */
    void deleteExpired();

    /**
     * Marca password reset como usado
     */
    void markAsUsed(PasswordResetId id);

    /**
     * Marca todos los password resets de un usuario como usados
     */
    void markAllAsUsedByUserId(Long userId);
}
