package com.impulse.features.auth.adapters.out.persistence;

import com.impulse.features.auth.application.port.out.UserRepository;
import com.impulse.features.auth.adapters.out.persistence.entity.UserJpaEntity;
import com.impulse.features.auth.adapters.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA implementation of UserRepository port
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;

        if (user.getId() == null) {
            // New user
            entity = new UserJpaEntity(
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName()
            );
            entity.setEmailVerified(user.isEmailVerified());
            entity.setIsActive(user.isActive());
        } else {
            // Update existing user
            entity = jpaRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + user.getId()));

            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setPasswordHash(user.getPasswordHash());
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            entity.setEmailVerified(user.isEmailVerified());
            entity.setIsActive(user.isActive());
        }

        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::mapToDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void updateEmailVerified(Long userId, boolean verified) {
        jpaRepository.updateEmailVerified(userId, verified);
    }

    @Override
    public void updatePassword(Long userId, String passwordHash) {
        jpaRepository.updatePassword(userId, passwordHash);
    }

    private User mapToDomain(UserJpaEntity entity) {
        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmailVerified(),
            entity.getIsActive()
        );
    }
}
