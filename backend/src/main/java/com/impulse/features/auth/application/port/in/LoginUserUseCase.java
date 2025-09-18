package com.impulse.features.auth.application.port.in;

import com.impulse.features.auth.application.dto.LoginUserCommand;
import com.impulse.features.auth.application.dto.LoginUserResponse;

/**
 * Inbound port for user login use case
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface LoginUserUseCase {
    LoginUserResponse execute(LoginUserCommand command);
}
