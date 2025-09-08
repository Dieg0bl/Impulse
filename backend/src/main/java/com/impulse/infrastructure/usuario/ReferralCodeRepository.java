package com.impulse.infrastructure.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.usuario.ReferralCode;
import java.util.Optional;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {
    Optional<ReferralCode> findByCode(String code);
    long countByUserId(Long userId);
}
