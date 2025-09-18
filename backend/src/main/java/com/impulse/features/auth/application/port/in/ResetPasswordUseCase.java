package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.ResetPasswordCommand;
import com.impulse.features.auth.application.dto.ResetPasswordResponse;

/**
 * ResetPasswordUseCase - Puerto de entrada para reset de contraseña
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface ResetPasswordUseCase {

    /**
     * Ejecuta el reset de contraseña con token
     * @param command comando con token y nueva contraseña
     * @return respuesta indicando éxito o fallo
     */
    ResetPasswordResponse execute(ResetPasswordCommand command);
}
