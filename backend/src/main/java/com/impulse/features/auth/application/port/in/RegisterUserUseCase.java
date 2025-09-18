package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.RegisterUserCommand;
import com.impulse.features.auth.application.dto.RegisterUserResponse;

/**
 * Inbound port for user registration use case
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface RegisterUserUseCase {
    RegisterUserResponse execute(RegisterUserCommand command);
}
