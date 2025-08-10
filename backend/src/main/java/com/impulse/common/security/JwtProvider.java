package com.impulse.common.security;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Proveedor empresarial de JWT con soporte para tokens de acceso y compatibilidad legacy.
 * Implementa estándares de seguridad OWASP para autenticación basada en tokens.
 */
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    // Duración para tokens de acceso cortos (15 minutos)
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);

    private Key key;

    @PostConstruct
    public void init() {
        // Verificar longitud mínima de secreto (256 bits = 32 bytes)
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret debe tener al menos 32 caracteres para seguridad");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Genera token legacy para compatibilidad con código existente.
     */
    public String generateToken(String username, String roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
    
    /**
     * Genera token de acceso de corta duración para cookies empresariales.
     */
    public String generateAccessToken(String userId, String userRole) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", userRole)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ACCESS_TOKEN_DURATION.toMillis()))
                .setIssuer("impulse-enterprise")
                .signWith(key)
                .compact();
    }

    /**
     * Valida token JWT verificando firma y expiración.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrae username del token (compatibilidad legacy).
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }
    
    /**
     * Extrae user ID del token (nuevo método empresarial).
     */
    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae roles del token (compatibilidad legacy).
     */
    public String getRolesFromToken(String token) {
        Claims claims = getClaims(token);
        // Intentar nuevo formato primero, luego legacy
        String role = (String) claims.get("role");
        return role != null ? role : (String) claims.get("roles");
    }
    
    /**
     * Extrae rol del usuario del token (nuevo método empresarial).
     */
    public String getUserRoleFromToken(String token) {
        Claims claims = getClaims(token);
        // Intentar nuevo formato primero, luego legacy
        String role = (String) claims.get("role");
        return role != null ? role : (String) claims.get("roles");
    }
    
    /**
     * Verifica si es un token de acceso empresarial.
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene tiempo restante de vida del token en segundos.
     */
    public long getTokenTimeToLive(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();
            return Math.max(0, (expiration.getTime() - now) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
