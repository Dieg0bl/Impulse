package com.impulse.features.auth.adapters.out.persistence.mapper;

import com.impulse.features.auth.domain.RefreshToken;
import com.impulse.features.auth.domain.RefreshTokenId;
import com.impulse.features.auth.adapters.out.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between RefreshToken domain entity and JPA entity
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Component
public class RefreshTokenJpaMapper {

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }

        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(
            domain.getId().getValue(),
            domain.getUserId(),
            domain.getTokenHash(),
            domain.getExpiresAt(),
            domain.getUserAgent(),
            domain.getIpAddress()
        );

        // Set optional fields if they exist
        if (domain.getRevokedAt() != null) {
            entity.setRevokedAt(domain.getRevokedAt());
            entity.setIsRevoked(true);
        }

        return entity;
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        // Create minimal RefreshToken for now - can be expanded later
        return RefreshToken.create(
            entity.getUserId(),
            "dummy-token", // This will be hashed anyway
            entity.getExpiresAt(),
            entity.getUserAgent(),
            entity.getIpAddress()
        );
    }
}
