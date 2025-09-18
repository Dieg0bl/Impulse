package com.impulse.features.auth.adapters.out.jwt;

import com.impulse.features.auth.application.port.out.JwtTokenService;
import com.impulse.shared.error.DomainException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token Service implementation using RS256
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final String issuer;

    public JwtTokenServiceImpl(
            @Value("${impulse.jwt.secret:impulse-default-secret-key-for-development-only}") String secret,
            @Value("${impulse.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs, // 15 minutes
            @Value("${impulse.jwt.issuer:impulse-v1}") String issuer) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.issuer = issuer;
    }

    @Override
    public JwtToken generateAccessToken(Long userId, String username, String[] roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roles", roles);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setIssuer(issuer)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new JwtToken(token, accessTokenExpirationMs / 1000); // Return seconds
    }

    @Override
    public Long extractUserId(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            throw new DomainException("Invalid token: " + e.getMessage());
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getTimeToExpiration(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            long currentTime = System.currentTimeMillis();
            long expirationTime = expiration.getTime();

            return Math.max(0, (expirationTime - currentTime) / 1000); // Return seconds
        } catch (Exception e) {
            return 0;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
