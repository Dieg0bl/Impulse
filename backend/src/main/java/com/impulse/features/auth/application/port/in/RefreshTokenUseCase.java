package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.RefreshTokenCommand;
import com.impulse.features.auth.application.dto.RefreshTokenResponse;

/**
 * RefreshTokenUseCase - Puerto de entrada para refresh de tokens
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface RefreshTokenUseCase {

    /**
     * Ejecuta el refresh de tokens con rotación automática
     * @param command comando con refresh token
     * @return respuesta con nuevos tokens o error
     */
    RefreshTokenResponse execute(RefreshTokenCommand command);
}
