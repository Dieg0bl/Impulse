package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * RefreshTokenRequest - DTO de request para refresh token endpoint
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class RefreshTokenRequest {

    private String refreshToken;
    private String userAgent;
    private String ipAddress;

    // Constructors
    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken, String userAgent, String ipAddress) {
        this.refreshToken = refreshToken;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Override
    public String toString() {
        return "RefreshTokenRequest{" +
                "refreshToken='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
