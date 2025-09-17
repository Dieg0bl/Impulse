package com.impulse.application.user.mappers;

import com.impulse.application.user.dto.UserResponse;
import com.impulse.domain.user.User;

/**
 * UserAppMapper - Mapper de la capa de aplicación para usuarios
 */
public class UserAppMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId().getValue(),
            user.getEmail().getValue(),
            user.getName(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getLastLoginAt(),
            user.isEmailVerified()
        );
    }
}
