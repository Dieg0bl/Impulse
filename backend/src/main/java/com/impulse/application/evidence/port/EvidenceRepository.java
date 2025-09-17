package com.impulse.application.evidence.port;

import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceId;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.EvidenceStatus;
import java.util.List;
import java.util.Optional;

/**
 * EvidenceRepository - Puerto del repositorio para Evidence
 */
public interface EvidenceRepository {

    /**
     * Guarda una evidencia
     */
    Evidence save(Evidence evidence);

    /**
     * Busca una evidencia por ID
     */
    Optional<Evidence> findById(EvidenceId evidenceId);

    /**
     * Busca evidencias por usuario
     */
    List<Evidence> findByUserId(UserId userId);

    /**
     * Busca evidencias por challenge
     */
    List<Evidence> findByChallengeId(ChallengeId challengeId);

    /**
     * Busca evidencias por status
     */
    List<Evidence> findByStatus(EvidenceStatus status);

    /**
     * Busca evidencias por usuario y challenge
     */
    List<Evidence> findByUserIdAndChallengeId(UserId userId, ChallengeId challengeId);

    /**
     * Busca evidencias pendientes de validación
     */
    List<Evidence> findPendingValidation();

    /**
     * Busca todas las evidencias
     */
    List<Evidence> findAll();

    /**
     * Elimina una evidencia por ID (soft delete)
     */
    void deleteById(EvidenceId evidenceId);

    /**
     * Verifica si existe una evidencia por ID
     */
    boolean existsById(EvidenceId evidenceId);

    /**
     * Cuenta el total de evidencias
     */
    long count();

    /**
     * Cuenta evidencias por status
     */
    long countByStatus(EvidenceStatus status);

    /**
     * Cuenta evidencias por challenge
     */
    long countByChallengeId(ChallengeId challengeId);
}
