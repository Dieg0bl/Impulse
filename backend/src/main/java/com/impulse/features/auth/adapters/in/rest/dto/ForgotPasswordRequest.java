package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * ForgotPasswordRequest - DTO de request para forgot password endpoint
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ForgotPasswordRequest {

    private String email;
    private String userAgent;
    private String ipAddress;

    // Constructors
    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email, String userAgent, String ipAddress) {
        this.email = email;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Override
    public String toString() {
        return "ForgotPasswordRequest{" +
                "email='" + email + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
