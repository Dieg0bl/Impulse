package com.impulse.domain.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_keys", indexes = { @Index(name = "idx_api_keys_hash", columnList = "key_hash") })
public class ApiKey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "key_hash", nullable = false, length = 64, unique = true)
    private String keyHash;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "last_used_at")
    private Instant lastUsedAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;

    public ApiKey(){}
    public ApiKey(String name, String keyHash){ this.name = name; this.keyHash = keyHash; }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getKeyHash(){ return keyHash; }
    public Instant getCreatedAt(){ return createdAt; }
    public Instant getLastUsedAt(){ return lastUsedAt; }
    public Instant getRevokedAt(){ return revokedAt; }
    public void markUsed(){ this.lastUsedAt = Instant.now(); }
    public void revoke(){ this.revokedAt = Instant.now(); }
    public boolean isRevoked(){ return revokedAt != null; }
}
