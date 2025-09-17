package com.impulse.infrastructure.persistence.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.domain.challenge.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // Buscar challenges por creador
    Page<Challenge> findByUserId(Long userId, Pageable pageable);

    // Buscar challenges por estado
    Page<Challenge> findByStatus(String status, Pageable pageable);

    // Buscar challenges activos
    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' AND c.startDate <= :now AND c.endDate >= :now")
    Page<Challenge> findActiveChallengess(@Param("now") LocalDateTime now, Pageable pageable);

    // Buscar challenges por tÃ­tulo que contenga texto
    @Query("SELECT c FROM Challenge c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Challenge> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    // Buscar challenges por descripciÃ³n que contenga texto
    @Query("SELECT c FROM Challenge c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Challenge> findByDescriptionContainingIgnoreCase(@Param("description") String description, Pageable pageable);

    // Buscar challenges por rango de fechas
    @Query("SELECT c FROM Challenge c WHERE c.startDate >= :startDate AND c.endDate <= :endDate")
    Page<Challenge> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    // Buscar challenges prÃ³ximos a empezar
    @Query("SELECT c FROM Challenge c WHERE c.startDate > :now AND c.startDate <= :futureDate")
    Page<Challenge> findUpcoming(@Param("now") LocalDateTime now,
                                 @Param("futureDate") LocalDateTime futureDate,
                                 Pageable pageable);

    // Buscar challenges terminados
    @Query("SELECT c FROM Challenge c WHERE c.endDate < :now")
    Page<Challenge> findFinished(@Param("now") LocalDateTime now, Pageable pageable);

    // Contar challenges por creador
    long countByUserId(Long userId);

    // Contar challenges activos
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.status = 'ACTIVE'")
    long countActive();
}

