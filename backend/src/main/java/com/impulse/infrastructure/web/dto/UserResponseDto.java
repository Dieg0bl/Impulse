package com.impulse.infrastructure.web.dto;

import com.impulse.domain.user.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for user response data
 */
@Data
public class UserResponseDto {
    
    private String id;
    private String email;
    private String username;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
