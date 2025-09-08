package com.impulse.infrastructure.web.dto;

import lombok.Data;

/**
 * DTO for authentication response
 */
@Data
public class AuthResponseDto {
    
    private String token;
    private String tokenType = "Bearer";
    private UserResponseDto user;
    
    public AuthResponseDto(String token, UserResponseDto user) {
        this.token = token;
        this.user = user;
    }
}
