package com.impulse.adapters.persistence.evidence.repository;

import com.impulse.adapters.persistence.evidence.entity.EvidenceJpaEntity;
import com.impulse.domain.enums.EvidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpringDataEvidenceRepository - Repositorio Spring Data para EvidenceJpaEntity
 */
@Repository
public interface SpringDataEvidenceRepository extends JpaRepository<EvidenceJpaEntity, Long> {

    List<EvidenceJpaEntity> findByUserId(Long userId);
    List<EvidenceJpaEntity> findByChallengeId(Long challengeId);
    List<EvidenceJpaEntity> findByStatus(EvidenceStatus status);
    List<EvidenceJpaEntity> findByUserIdAndChallengeId(Long userId, Long challengeId);

    @Query("SELECT e FROM EvidenceJpaEntity e WHERE e.status = 'PENDING_VALIDATION'")
    List<EvidenceJpaEntity> findPendingValidation();

    @Query("SELECT e FROM EvidenceJpaEntity e WHERE e.id = :id AND e.status != 'ARCHIVED'")
    Optional<EvidenceJpaEntity> findByIdAndNotArchived(@Param("id") Long id);

    long countByStatus(EvidenceStatus status);
    long countByChallengeId(Long challengeId);
}


