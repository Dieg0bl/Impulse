package com.impulse.lean.infrastructure.security;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * IMPULSE LEAN v1 - JWT Token Service
 * 
 * Handles JWT token generation, validation and parsing
 * Integrates with domain User entity
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Component
public class JwtTokenService {

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;

    public JwtTokenService(@Value("${impulse.jwt.secret}") String secret,
                          @Value("${impulse.jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        return generateTokenForUser(userPrincipal.getUser());
    }

    /**
     * Generate JWT token for user entity
     */
    public String generateTokenForUser(User user) {
        Date validity = new Date(System.currentTimeMillis() + tokenValidityInMilliseconds);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("displayName", user.getFullName());
        claims.put("isActive", user.isActive());
        claims.put("isLocked", user.isLocked());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(User user) {
        Date validity = new Date(System.currentTimeMillis() + (tokenValidityInMilliseconds * 7)); // 7x longer
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Get user role from JWT token
     */
    public UserRole getUserRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String roleName = claims.get("role", String.class);
        return UserRole.valueOf(roleName);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get expiration date from token
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Get all claims from JWT token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract token from Authorization header
     */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Create JWT token response for API
     */
    public JwtTokenResponse createTokenResponse(User user, String accessToken, String refreshToken) {
        return new JwtTokenResponse(
            accessToken,
            refreshToken,
            "Bearer",
            tokenValidityInMilliseconds / 1000,
            user.getEmail(),
            user.getRole().name(),
            user.getFullName()
        );
    }

    /**
     * JWT Token Response DTO
     */
    public static class JwtTokenResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType;
        private final long expiresIn;
        private final String username;
        private final String role;
        private final String displayName;

        public JwtTokenResponse(String accessToken, String refreshToken, String tokenType, 
                              long expiresIn, String username, String role, String displayName) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.username = username;
            this.role = role;
            this.displayName = displayName;
        }

        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public String getTokenType() { return tokenType; }
        public long getExpiresIn() { return expiresIn; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getDisplayName() { return displayName; }
    }
}
