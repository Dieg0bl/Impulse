package com.impulse.domain.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para tokens de autenticación (magic links / futuros refresh / recovery).
 * Diseño minimalista para evitar sobre-ingeniería en fase temprana.
 */
@Entity
@Table(name = "auth_tokens", indexes = {
    @Index(name = "idx_auth_tokens_token", columnList = "token", unique = true),
    @Index(name = "idx_auth_tokens_user", columnList = "userId")
})
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, unique = true, length = 64)
    private String token;
    @Column(nullable = false, length = 20)
    private String type; // MAGIC, REFRESH, RECOVERY
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean used = false;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuthToken() {}
    public AuthToken(Long userId, String token, String type, LocalDateTime expiresAt) {
        this.userId = userId; this.token = token; this.type = type; this.expiresAt = expiresAt;
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getToken() { return token; }
    public String getType() { return type; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void markUsed() { this.used = true; }
    public boolean isExpired() { return expiresAt.isBefore(LocalDateTime.now()); }
}
