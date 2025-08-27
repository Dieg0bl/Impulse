package com.impulse.application.ports;

import com.impulse.domain.auth.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AuthTokenPort extends JpaRepository<AuthToken, Long> {
	java.util.Optional<AuthToken> findByToken(String token);
}
