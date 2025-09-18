package com.impulse.features.auth.adapters.in.rest.mapper;

import com.impulse.features.auth.application.dto.RegisterUserCommand;
import com.impulse.features.auth.application.dto.RegisterUserResponse;
import com.impulse.features.auth.application.dto.LoginUserCommand;
import com.impulse.features.auth.application.dto.LoginUserResponse;
import com.impulse.features.auth.adapters.in.rest.dto.AuthApiRegisterRequest;
import com.impulse.features.auth.adapters.in.rest.dto.AuthApiRegisterResponse;
import com.impulse.features.auth.adapters.in.rest.dto.AuthApiLoginRequest;
import com.impulse.features.auth.adapters.in.rest.dto.AuthApiLoginResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper between REST API DTOs and Application DTOs
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Component
public class AuthApiMapper {

    public RegisterUserCommand toRegisterCommand(AuthApiRegisterRequest request,
                                               String userAgent, String ipAddress) {
        return new RegisterUserCommand(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            userAgent,
            ipAddress
        );
    }

    public AuthApiRegisterResponse toRegisterApiResponse(RegisterUserResponse response,
                                                       String correlationId) {
        AuthApiRegisterResponse.RegisterData data = new AuthApiRegisterResponse.RegisterData(
            response.getUserId(),
            response.getUsername(),
            response.getEmail(),
            response.isEmailVerificationRequired()
        );

        return new AuthApiRegisterResponse(
            "REGISTRATION_SUCCESS",
            response.getMessage(),
            correlationId,
            data
        );
    }

    public LoginUserCommand toLoginCommand(AuthApiLoginRequest request,
                                         String userAgent, String ipAddress) {
        return new LoginUserCommand(
            request.getUsernameOrEmail(),
            request.getPassword(),
            userAgent,
            ipAddress
        );
    }

    public AuthApiLoginResponse toLoginApiResponse(LoginUserResponse response,
                                                 String correlationId) {
        AuthApiLoginResponse.LoginData data = new AuthApiLoginResponse.LoginData(
            response.getUserId(),
            response.getUsername(),
            response.getAccessToken(),
            response.getRefreshToken(),
            response.isEmailVerified(),
            response.getExpiresIn()
        );

        return new AuthApiLoginResponse(
            "LOGIN_SUCCESS",
            "Login successful",
            correlationId,
            data
        );
    }
}
