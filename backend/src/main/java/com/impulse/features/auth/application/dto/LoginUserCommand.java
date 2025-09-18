package com.impulse.features.auth.application.dto;

import java.util.Objects;

/**
 * Command DTO for user login
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class LoginUserCommand {
    private final String usernameOrEmail;
    private final String password;
    private final String userAgent;
    private final String ipAddress;

    public LoginUserCommand(String usernameOrEmail, String password,
                          String userAgent, String ipAddress) {
        this.usernameOrEmail = Objects.requireNonNull(usernameOrEmail, "Username/Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public String getPassword() { return password; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginUserCommand that = (LoginUserCommand) o;
        return Objects.equals(usernameOrEmail, that.usernameOrEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameOrEmail);
    }
}
