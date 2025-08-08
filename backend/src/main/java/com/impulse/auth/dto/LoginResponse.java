package com.impulse.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para respuestas de login empresarial con datos de sesión seguros.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    
    private boolean success;
    private String message;
    private String token; // Solo para compatibilidad legacy, null en modo cookie
    private String role;
    private Long userId;
    private String username;
    
    // Constructor vacío para Jackson
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message, String token, String role, Long userId, String username) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.username = username;
    }
    
    // Constructor para respuestas de error
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
