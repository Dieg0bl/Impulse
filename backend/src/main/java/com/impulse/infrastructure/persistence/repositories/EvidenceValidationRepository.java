package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.model.EvidenceValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EvidenceValidationRepository extends JpaRepository<EvidenceValidation, Long> {

    // Buscar validaciones por evidencia
    Page<EvidenceValidation> findByEvidenceId(Long evidenceId, Pageable pageable);

    // Buscar validaciones por validador
    Page<EvidenceValidation> findByValidatorId(Long validatorId, Pageable pageable);

    // Buscar validaciones por tipo
    Page<EvidenceValidation> findByValidationType(String validationType, Pageable pageable);

    // Buscar validaciones por estado
    Page<EvidenceValidation> findByStatus(String status, Pageable pageable);

    // Buscar validaciones por rango de fechas
    @Query("SELECT ev FROM EvidenceValidation ev WHERE ev.validatedAt BETWEEN :startDate AND :endDate")
    Page<EvidenceValidation> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    // Buscar validaciones pendientes
    @Query("SELECT ev FROM EvidenceValidation ev WHERE ev.status = 'PENDING'")
    Page<EvidenceValidation> findPending(Pageable pageable);

    // Buscar validaciones aprobadas
    @Query("SELECT ev FROM EvidenceValidation ev WHERE ev.status = 'APPROVED'")
    Page<EvidenceValidation> findApproved(Pageable pageable);

    // Buscar validaciones rechazadas
    @Query("SELECT ev FROM EvidenceValidation ev WHERE ev.status = 'REJECTED'")
    Page<EvidenceValidation> findRejected(Pageable pageable);

    // Contar validaciones por validador
    long countByValidatorId(Long validatorId);

    // Contar validaciones por evidencia
    long countByEvidenceId(Long evidenceId);

    // Buscar última validación de una evidencia
    @Query("SELECT ev FROM EvidenceValidation ev WHERE ev.evidenceId = :evidenceId ORDER BY ev.validatedAt DESC")
    Page<EvidenceValidation> findLatestByEvidenceId(@Param("evidenceId") Long evidenceId, Pageable pageable);
}
