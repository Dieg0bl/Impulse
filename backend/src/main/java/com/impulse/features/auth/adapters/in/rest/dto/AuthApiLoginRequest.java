package com.impulse.features.auth.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * API Request DTO for user login
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class AuthApiLoginRequest {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor for Jackson
    public AuthApiLoginRequest() {
        // Required for JSON deserialization
    }

    // Getters and setters
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
