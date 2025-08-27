package com.impulse.infrastructure.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.usuario.ReferralCode;
import java.util.Optional;
import com.impulse.domain.usuario.ReferralCodeRepositoryPort;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long>, ReferralCodeRepositoryPort {
    Optional<ReferralCode> findByCode(String code);
    long countByUserId(Long userId);

    // Adapter default methods to satisfy the domain port without breaking JpaRepository signatures
    default Optional<ReferralCode> find(Long id) { return findById(id); }
    default ReferralCode store(ReferralCode code) { return save(code); }
    default java.util.List<ReferralCode> listAll() { return findAll(); }
    default void removeById(Long id) { deleteById(id); }
}
