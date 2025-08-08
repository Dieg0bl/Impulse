package com.impulse.infrastructure.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.auth.AuthToken;

/**
 * Repositorio para tokens de autenticación.
 * Solo acceso a datos; la lógica reside en AuthService.
 */
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
}
