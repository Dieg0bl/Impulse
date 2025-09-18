package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * VerifyEmailRequest - DTO de request para verify email endpoint
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class VerifyEmailRequest {

    private String token;
    private String userAgent;
    private String ipAddress;

    // Constructors
    public VerifyEmailRequest() {}

    public VerifyEmailRequest(String token, String userAgent, String ipAddress) {
        this.token = token;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Override
    public String toString() {
        return "VerifyEmailRequest{" +
                "token='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
