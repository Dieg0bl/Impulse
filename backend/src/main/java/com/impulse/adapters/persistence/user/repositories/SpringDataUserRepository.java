package com.impulse.adapters.persistence.user.repositories;

import com.impulse.adapters.persistence.user.entities.UserJpaEntity;
import com.impulse.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SpringDataUserRepository - Repositorio Spring Data para UserJpaEntity
 */
@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserJpaEntity> findByRole(UserRole role, Pageable pageable);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.status = 'ACTIVE'")
    Page<UserJpaEntity> findActiveUsers(Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.emailVerified = true")
    long countVerifiedUsers();
}


