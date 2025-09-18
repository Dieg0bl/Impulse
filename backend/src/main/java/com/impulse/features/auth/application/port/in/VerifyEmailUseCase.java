package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.VerifyEmailCommand;
import com.impulse.features.auth.application.dto.VerifyEmailResponse;

/**
 * VerifyEmailUseCase - Puerto de entrada para verificación de email
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface VerifyEmailUseCase {

    /**
     * Ejecuta la verificación de email con token
     * @param command comando con token de verificación
     * @return respuesta indicando éxito o fallo
     */
    VerifyEmailResponse execute(VerifyEmailCommand command);
}
