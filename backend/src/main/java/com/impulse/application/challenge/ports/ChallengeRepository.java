package com.impulse.application.challenge.ports;

import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import java.util.Optional;
import java.util.List;

/**
 * ChallengeRepository - Puerto de salida para persistencia de challenges
 */
public interface ChallengeRepository {

    /**
     * Guarda un challenge (crear o actualizar)
     */
    Challenge save(Challenge challenge);

    /**
     * Busca un challenge por ID
     */
    Optional<Challenge> findById(ChallengeId id);

    /**
     * Lista todos los challenges con paginación
     */
    List<Challenge> findAll(int page, int size);

    /**
     * Lista challenges por creador
     */
    List<Challenge> findByCreator(UserId creatorId, int page, int size);

    /**
     * Lista challenges por estado
     */
    List<Challenge> findByStatus(ChallengeStatus status, int page, int size);

    /**
     * Lista challenges por tipo
     */
    List<Challenge> findByType(ChallengeType type, int page, int size);

    /**
     * Lista challenges activos
     */
    List<Challenge> findActiveChallenge(int page, int size);

    /**
     * Cuenta el total de challenges
     */
    long count();

    /**
     * Cuenta challenges por estado
     */
    long countByStatus(ChallengeStatus status);

    /**
     * Elimina un challenge por ID
     */
    void deleteById(ChallengeId id);

    /**
     * Verifica si existe un challenge con el ID dado
     */
    boolean existsById(ChallengeId id);
}
