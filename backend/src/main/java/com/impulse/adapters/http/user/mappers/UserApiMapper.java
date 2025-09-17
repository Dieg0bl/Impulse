package com.impulse.adapters.http.user.mappers;

import com.impulse.adapters.http.user.dto.CreateUserRequestDto;
import com.impulse.adapters.http.user.dto.UserResponseDto;
import com.impulse.application.user.dto.CreateUserCommand;
import com.impulse.application.user.dto.UserResponse;
import org.springframework.stereotype.Component;

/**
 * UserApiMapper - Mapper de la capa HTTP para usuarios
 */
@Component
public class UserApiMapper {

    /**
     * Convierte de DTO de request HTTP a comando de aplicación
     */
    public CreateUserCommand toCommand(CreateUserRequestDto requestDto) {
        return new CreateUserCommand(
            requestDto.getEmail(),
            requestDto.getName(),
            requestDto.getPassword(),
            requestDto.getRole()
        );
    }

    /**
     * Convierte de respuesta de aplicación a DTO de response HTTP
     */
    public UserResponseDto toResponseDto(UserResponse response) {
        UserResponseDto dto = new UserResponseDto(
            response.getId(),
            response.getEmail(),
            response.getName(),
            response.getRole(),
            response.getStatus()
        );

        dto.setCreatedAt(response.getCreatedAt());
        dto.setLastLoginAt(response.getLastLoginAt());
        dto.setEmailVerified(response.isEmailVerified());

        return dto;
    }
}
