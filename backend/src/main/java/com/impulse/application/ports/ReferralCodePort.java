package com.impulse.application.ports;

import com.impulse.domain.usuario.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReferralCodePort extends JpaRepository<ReferralCode, Long> {
    long countByUserId(Long userId);
}
