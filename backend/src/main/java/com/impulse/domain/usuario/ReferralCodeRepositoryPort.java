package com.impulse.domain.usuario;

import java.util.Optional;
import java.util.List;

public interface ReferralCodeRepositoryPort {
    Optional<ReferralCode> find(Long id);
    ReferralCode store(ReferralCode code);
    List<ReferralCode> listAll();
    void removeById(Long id);
    java.util.Optional<ReferralCode> findByCode(String code);
    long countByUserId(Long userId);
}
