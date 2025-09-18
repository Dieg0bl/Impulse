package com.impulse.features.auth.adapters.out.persistence;

import com.impulse.features.auth.application.port.out.RefreshTokenRepository;
import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.features.auth.domain.RefreshTokenId;
import com.impulse.features.auth.adapters.out.persistence.entity.RefreshTokenJpaEntity;
import com.impulse.features.auth.adapters.out.persistence.repository.RefreshTokenJpaRepository;
import com.impulse.features.auth.adapters.out.persistence.mapper.RefreshTokenJpaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of RefreshTokenRepository port
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
@Transactional
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenJpaMapper mapper;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpaRepository,
                                    RefreshTokenJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = mapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findById(RefreshTokenId id) {
        Optional<RefreshTokenJpaEntity> entity = jpaRepository.findById(id.getValue());
        return entity.map(mapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        Optional<RefreshTokenJpaEntity> entity = jpaRepository.findByTokenHash(tokenHash);
        return entity.map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findActiveByUserId(Long userId) {
        List<RefreshTokenJpaEntity> entities = jpaRepository.findActiveByUserId(userId, LocalDateTime.now());
        return entities.stream()
                      .map(mapper::toDomain)
                      .collect(Collectors.toList());
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        jpaRepository.deleteById(refreshToken.getId().getValue());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        jpaRepository.revokeAllByUserId(userId, LocalDateTime.now());
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
