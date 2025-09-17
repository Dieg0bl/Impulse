package com.impulse.adapters.persistence.evidencevalidation.repository;

import com.impulse.adapters.persistence.evidencevalidation.entity.EvidenceValidationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEvidenceValidationRepository extends JpaRepository<EvidenceValidationJpaEntity, UUID> {

    List<EvidenceValidationJpaEntity> findByEvidenceIdOrderByValidatedAtDesc(UUID evidenceId);

    List<EvidenceValidationJpaEntity> findByValidatorIdOrderByValidatedAtDesc(UUID validatorId);

    Page<EvidenceValidationJpaEntity> findByEvidenceId(UUID evidenceId, Pageable pageable);

    Page<EvidenceValidationJpaEntity> findByValidatorId(UUID validatorId, Pageable pageable);

    Page<EvidenceValidationJpaEntity> findByStatus(String status, Pageable pageable);

    Page<EvidenceValidationJpaEntity> findByValidationType(String validationType, Pageable pageable);

    @Query("SELECT ev FROM EvidenceValidationJpaEntity ev WHERE ev.validatedAt BETWEEN :startDate AND :endDate")
    Page<EvidenceValidationJpaEntity> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);

    @Query("SELECT ev FROM EvidenceValidationJpaEntity ev WHERE ev.status = 'PENDING' ORDER BY ev.createdAt ASC")
    Page<EvidenceValidationJpaEntity> findPending(Pageable pageable);

    @Query("SELECT ev FROM EvidenceValidationJpaEntity ev WHERE ev.status = 'APPROVED'")
    Page<EvidenceValidationJpaEntity> findApproved(Pageable pageable);

    @Query("SELECT ev FROM EvidenceValidationJpaEntity ev WHERE ev.status = 'REJECTED'")
    Page<EvidenceValidationJpaEntity> findRejected(Pageable pageable);

    long countByValidatorId(UUID validatorId);

    long countByEvidenceId(UUID evidenceId);

    @Query("SELECT ev FROM EvidenceValidationJpaEntity ev WHERE ev.evidenceId = :evidenceId ORDER BY ev.validatedAt DESC")
    Page<EvidenceValidationJpaEntity> findLatestByEvidenceId(@Param("evidenceId") UUID evidenceId, Pageable pageable);

    @Query("SELECT AVG(ev.score) FROM EvidenceValidationJpaEntity ev WHERE ev.evidenceId = :evidenceId AND ev.score IS NOT NULL")
    Double getAverageScoreByEvidence(@Param("evidenceId") UUID evidenceId);
}
