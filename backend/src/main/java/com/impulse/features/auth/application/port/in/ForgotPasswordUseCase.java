package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.ForgotPasswordCommand;
import com.impulse.features.auth.application.dto.ForgotPasswordResponse;

/**
 * ForgotPasswordUseCase - Puerto de entrada para forgot password
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public interface ForgotPasswordUseCase {

    /**
     * Procesa solicitud de reset de contraseña
     * @param command Comando con email y metadatos
     * @return Respuesta siempre exitosa (por seguridad)
     */
    ForgotPasswordResponse execute(ForgotPasswordCommand command);
}
