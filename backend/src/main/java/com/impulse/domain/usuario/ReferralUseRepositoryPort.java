package com.impulse.domain.usuario;

import java.util.Optional;
import java.util.List;

public interface ReferralUseRepositoryPort {
    Optional<ReferralUse> find(Long id);
    ReferralUse store(ReferralUse use);
    List<ReferralUse> listAll();
    void removeById(Long id);
    long countByCodeId(Long codeId);
    long countByReferredUserId(Long referredUserId);
}
