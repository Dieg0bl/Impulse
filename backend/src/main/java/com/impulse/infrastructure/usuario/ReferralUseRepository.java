package com.impulse.infrastructure.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.usuario.ReferralUse;
import java.util.Optional;
import java.util.List;
import com.impulse.domain.usuario.ReferralUseRepositoryPort;

@Repository
public interface ReferralUseRepository extends JpaRepository<ReferralUse, Long>, ReferralUseRepositoryPort {
    long countByCodeId(Long codeId);
    long countByReferredUserId(Long referredUserId);

    default Optional<ReferralUse> find(Long id) { return findById(id); }
    default ReferralUse store(ReferralUse use) { return save(use); }
    default java.util.List<ReferralUse> listAll() { return findAll(); }
    default void removeById(Long id) { deleteById(id); }
}
