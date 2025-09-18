package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.LogoutUserCommand;
import com.impulse.features.auth.application.dto.LogoutUserResponse;

/**
 * LogoutUserUseCase - Puerto de entrada para logout de usuario
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface LogoutUserUseCase {

    /**
     * Ejecuta el logout del usuario con invalidación de tokens
     * @param command comando con refresh token y opciones de logout
     * @return respuesta indicando éxito y cantidad de tokens invalidados
     */
    LogoutUserResponse execute(LogoutUserCommand command);
}
