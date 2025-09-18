package com.impulse.features.auth.adapters.out.persistence;

import com.impulse.features.auth.application.port.out.EmailVerificationRepository;
import com.impulse.features.auth.domain.EmailVerification;
import com.impulse.features.auth.domain.EmailVerificationId;
import com.impulse.features.auth.adapters.out.persistence.entity.EmailVerificationJpaEntity;
import com.impulse.features.auth.adapters.out.persistence.repository.EmailVerificationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JPA implementation of EmailVerificationRepository port
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
@Transactional
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

    private final EmailVerificationJpaRepository jpaRepository;

    public EmailVerificationRepositoryImpl(EmailVerificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        EmailVerificationJpaEntity entity = mapToJpaEntity(emailVerification);
        EmailVerificationJpaEntity savedEntity = jpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<EmailVerification> findById(EmailVerificationId id) {
        return jpaRepository.findById(id.getValue()).map(this::mapToDomain);
    }

    @Override
    public Optional<EmailVerification> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::mapToDomain);
    }

    @Override
    public Optional<EmailVerification> findByUserIdAndEmail(Long userId, String email) {
        return jpaRepository.findActiveByUserIdAndEmail(userId, email, LocalDateTime.now())
                            .map(this::mapToDomain);
    }

    @Override
    public void delete(EmailVerification emailVerification) {
        jpaRepository.deleteById(emailVerification.getId().getValue());
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredOrUsedTokens(LocalDateTime.now());
    }

    private EmailVerificationJpaEntity mapToJpaEntity(EmailVerification domain) {
        EmailVerificationJpaEntity entity = new EmailVerificationJpaEntity(
            domain.getId().getValue(),
            domain.getUserId(),
            domain.getEmail(),
            domain.getTokenHash(),
            domain.getExpiresAt(),
            domain.getCreatedAt(),
            domain.getUserAgent(),
            domain.getIpAddress()
        );

        entity.setVerifiedAt(domain.getVerifiedAt());
        return entity;
    }

    private EmailVerification mapToDomain(EmailVerificationJpaEntity entity) {
        return new EmailVerification(
            EmailVerificationId.of(entity.getId()),
            entity.getUserId(),
            entity.getEmail(),
            entity.getTokenHash(),
            entity.getExpiresAt(),
            entity.getVerifiedAt(),
            entity.getUserAgent(),
            entity.getIpAddress(),
            entity.getCreatedAt()
        );
    }
}
