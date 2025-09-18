package com.impulse.features.auth.application.dto;

import java.util.Objects;

/**
 * Command DTO for user registration
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class RegisterUserCommand {
    private final String username;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String userAgent;
    private final String ipAddress;

    public RegisterUserCommand(String username, String email, String password,
                             String firstName, String lastName, String userAgent, String ipAddress) {
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterUserCommand that = (RegisterUserCommand) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
