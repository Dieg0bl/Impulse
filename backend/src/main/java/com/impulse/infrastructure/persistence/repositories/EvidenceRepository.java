package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.model.Evidence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    // Buscar evidencias por usuario
    Page<Evidence> findByUserId(Long userId, Pageable pageable);

    // Buscar evidencias por challenge
    Page<Evidence> findByChallengeId(Long challengeId, Pageable pageable);

    // Buscar evidencias por usuario y challenge
    Page<Evidence> findByUserIdAndChallengeId(Long userId, Long challengeId, Pageable pageable);

    // Buscar evidencias por estado
    Page<Evidence> findByStatus(String status, Pageable pageable);

    // Buscar evidencias por tipo
    Page<Evidence> findByType(String type, Pageable pageable);

    // Buscar evidencias por rango de fechas
    @Query("SELECT e FROM Evidence e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Page<Evidence> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    // Contar evidencias por usuario
    long countByUserId(Long userId);

    // Contar evidencias por challenge
    long countByChallengeId(Long challengeId);

    // Buscar evidencias pendientes de validación
    @Query("SELECT e FROM Evidence e WHERE e.status = 'PENDING_VALIDATION'")
    Page<Evidence> findPendingValidation(Pageable pageable);

    // Buscar evidencias validadas
    @Query("SELECT e FROM Evidence e WHERE e.status = 'VALIDATED'")
    Page<Evidence> findValidated(Pageable pageable);

    // Buscar evidencias rechazadas
    @Query("SELECT e FROM Evidence e WHERE e.status = 'REJECTED'")
    Page<Evidence> findRejected(Pageable pageable);
}
