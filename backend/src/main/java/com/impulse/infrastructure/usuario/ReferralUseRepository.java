package com.impulse.infrastructure.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.usuario.ReferralUse;

@Repository
public interface ReferralUseRepository extends JpaRepository<ReferralUse, Long> {
    long countByCodeId(Long codeId);
    long countByReferredUserId(Long referredUserId);
}
