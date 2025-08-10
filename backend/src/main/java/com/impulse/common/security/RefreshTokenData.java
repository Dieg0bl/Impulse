package com.impulse.common.security;

import java.time.LocalDateTime;

/**
 * Datos del refresh token para el cache empresarial
 */
public class RefreshTokenData {
    
    private final String userId;
    private final String userRole;
    private final String sessionId;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final String csrfToken;
    private final String userAgent;
    private final String ipAddress;
    
    public RefreshTokenData(String userId, String userRole, LocalDateTime expiresAt, 
                           String csrfToken, String ipAddress, String userAgent) {
        this.userId = userId;
        this.userRole = userRole;
        this.sessionId = java.util.UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.csrfToken = csrfToken;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }
    
    /**
     * Verifica si el token ha expirado
     */
    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }
    
    /**
     * Verifica si el token es válido para la sesión actual
     */
    public boolean isValidForSession(String userId, String userAgent, String ipAddress) {
        return this.userId.equals(userId) &&
               this.userAgent.equals(userAgent) &&
               this.ipAddress.equals(ipAddress);
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public String getCsrfToken() {
        return csrfToken;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    @Override
    public String toString() {
        return String.format("RefreshTokenData{userId='%s', userRole='%s', sessionId='%s', created='%s', expires='%s'}", 
                           userId, userRole, sessionId, createdAt, expiresAt);
    }
}
