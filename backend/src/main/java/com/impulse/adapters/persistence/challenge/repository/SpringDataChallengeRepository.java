package com.impulse.adapters.persistence.challenge.repository;

import com.impulse.adapters.persistence.challenge.entity.ChallengeJpaEntity;
import com.impulse.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpringDataChallengeRepository - Repositorio Spring Data para ChallengeJpaEntity
 */
@Repository
public interface SpringDataChallengeRepository extends JpaRepository<ChallengeJpaEntity, Long> {

    /**
     * Encuentra challenges por status
     */
    List<ChallengeJpaEntity> findByStatus(ChallengeStatus status);

    /**
     * Encuentra challenges creados por un usuario especÃ­fico
     */
    List<ChallengeJpaEntity> findByCreatedBy(Long createdBy);

    /**
     * Encuentra challenges por tipo
     */
    List<ChallengeJpaEntity> findByType(String type);

    /**
     * Encuentra challenges activos (status ACTIVE)
     */
    @Query("SELECT c FROM ChallengeJpaEntity c WHERE c.status = 'ACTIVE'")
    List<ChallengeJpaEntity> findActiveChallenges();

    /**
     * Encuentra challenge por ID que no estÃ© eliminado
     */
    @Query("SELECT c FROM ChallengeJpaEntity c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<ChallengeJpaEntity> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Cuenta challenges por status
     */
    long countByStatus(ChallengeStatus status);
}


