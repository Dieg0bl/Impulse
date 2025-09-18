package com.impulse.features.auth.adapters.out.persistence.repository;

import com.impulse.features.auth.adapters.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.emailVerified = :verified WHERE u.id = :userId")
    int updateEmailVerified(@Param("userId") Long userId, @Param("verified") Boolean verified);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.passwordHash = :passwordHash WHERE u.id = :userId")
    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);
}
