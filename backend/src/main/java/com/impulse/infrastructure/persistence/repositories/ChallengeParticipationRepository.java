package com.impulse.infrastructure.persistence.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.domain.model.ChallengeParticipation;
import com.impulse.domain.model.Challenge;
import com.impulse.domain.model.User;
import com.impulse.domain.enums.ParticipationStatus;

/**
 * Repository interface for ChallengeParticipation entity operations
 */
@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    /**
     * Find participation by challenge and user
     */
    Optional<ChallengeParticipation> findByChallengeAndUser(Challenge challenge, User user);

    /**
     * Find participation by challenge ID and user ID
     */
    Optional<ChallengeParticipation> findByChallengeIdAndUserId(Long challengeId, Long userId);

    /**
     * Find all participations for a user
     */
    List<ChallengeParticipation> findByUser(User user);

    /**
     * Find all participations for a user ID
     */
    List<ChallengeParticipation> findByUserId(Long userId);

    /**
     * Find all participations for a challenge
     */
    List<ChallengeParticipation> findByChallenge(Challenge challenge);

    /**
     * Find all participations for a challenge ID
     */
    List<ChallengeParticipation> findByChallengeId(Long challengeId);

    /**
     * Find participations by status
     */
    List<ChallengeParticipation> findByStatus(ParticipationStatus status);

    /**
     * Find active participations for user
     */
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.user.id = :userId " +
           "AND cp.status = 'ACTIVE' ORDER BY cp.joinedAt DESC")
    List<ChallengeParticipation> findActiveParticipationsByUser(@Param("userId") Long userId);

    /**
     * Find completed participations for user
     */
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.user.id = :userId " +
           "AND cp.status = 'COMPLETED' ORDER BY cp.completedAt DESC")
    List<ChallengeParticipation> findCompletedParticipationsByUser(@Param("userId") Long userId);

    /**
     * Count participations by challenge
     */
    Long countByChallenge(Challenge challenge);

    /**
     * Count participations by challenge ID
     */
    Long countByChallengeId(Long challengeId);

    /**
     * Count active participations by challenge
     */
    @Query("SELECT COUNT(cp) FROM ChallengeParticipation cp WHERE cp.challenge.id = :challengeId " +
           "AND cp.status = 'ACTIVE'")
    Long countActiveParticipationsByChallenge(@Param("challengeId") Long challengeId);

    /**
     * Check if user is participating in challenge
     */
    @Query("SELECT COUNT(cp) > 0 FROM ChallengeParticipation cp WHERE cp.user.id = :userId " +
           "AND cp.challenge.id = :challengeId AND cp.status != 'LEFT'")
    Boolean isUserParticipatingInChallenge(@Param("userId") Long userId,
                                          @Param("challengeId") Long challengeId);

    /**
     * Find participations with pagination
     */
    Page<ChallengeParticipation> findByStatus(ParticipationStatus status, Pageable pageable);

    /**
     * Find participations by user with pagination
     */
    Page<ChallengeParticipation> findByUser(User user, Pageable pageable);

    /**
     * Find participations by challenge with pagination
     */
    Page<ChallengeParticipation> findByChallenge(Challenge challenge, Pageable pageable);

    /**
     * Find participations by date range
     */
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.joinedAt BETWEEN :startDate AND :endDate")
    List<ChallengeParticipation> findByJoinedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find top participants by completion rate
     */
    @Query("SELECT cp.user, COUNT(cp) as completedCount FROM ChallengeParticipation cp " +
           "WHERE cp.status = 'COMPLETED' GROUP BY cp.user ORDER BY completedCount DESC")
    List<Object[]> findTopParticipantsByCompletionRate(Pageable pageable);

    /**
     * Find participations ending soon
     */
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.challenge.endDate BETWEEN :now AND :soonTime " +
           "AND cp.status = 'ACTIVE'")
    List<ChallengeParticipation> findParticipationsEndingSoon(@Param("now") LocalDateTime now,
                                                              @Param("soonTime") LocalDateTime soonTime);

    /**
     * Update participation status
     */
    @Query("UPDATE ChallengeParticipation cp SET cp.status = :status, cp.updatedAt = :updateTime " +
           "WHERE cp.id = :participationId")
    void updateStatus(@Param("participationId") Long participationId,
                     @Param("status") ParticipationStatus status,
                     @Param("updateTime") LocalDateTime updateTime);

    /**
     * Find expired participations
     */
    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.challenge.endDate < :currentTime " +
           "AND cp.status = 'ACTIVE'")
    List<ChallengeParticipation> findExpiredParticipations(@Param("currentTime") LocalDateTime currentTime);
}
