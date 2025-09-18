package com.impulse.features.auth.application.port.out;

/**
 * Outbound port for JWT token generation and validation
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface JwtTokenService {

    /**
     * JWT token representation
     */
    public static class JwtToken {
        private final String token;
        private final long expiresIn; // seconds

        public JwtToken(String token, long expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }

        public String getToken() { return token; }
        public long getExpiresIn() { return expiresIn; }
    }

    /**
     * Generate access token for authenticated user
     */
    JwtToken generateAccessToken(Long userId, String username, String[] roles);

    /**
     * Validate and extract user ID from token
     */
    Long extractUserId(String token);

    /**
     * Validate token without extracting claims
     */
    boolean isTokenValid(String token);

    /**
     * Get remaining time to expiration in seconds
     */
    long getTimeToExpiration(String token);
}
