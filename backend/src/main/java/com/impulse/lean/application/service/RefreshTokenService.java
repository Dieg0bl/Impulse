package com.impulse.lean.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.impulse.lean.domain.model.RefreshToken;
import com.impulse.lean.domain.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${impulse.security.jwt.refresh-expiration-ms:1209600000}")
    private long refreshExpiryMs;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken create(String username) {
        RefreshToken t = new RefreshToken();
        t.setUsername(username);
        t.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiryMs/1000));
        return repo.save(t);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }

    public void revoke(RefreshToken t) {
        t.setRevoked(true);
        repo.save(t);
    }
}
