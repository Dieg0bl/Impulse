package com.impulse.lean.domain.repository;

import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.ParticipationStatus;
import com.impulse.lean.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Challenge Participation Repository Interface
 * 
 * Repository for challenge participation domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    // Basic lookups
    Optional<ChallengeParticipation> findById(Long id);
    Optional<ChallengeParticipation> findByChallengeAndUser(Challenge challenge, User user);
    
    boolean existsByChallengeAndUser(Challenge challenge, User user);

    // User queries
    List<ChallengeParticipation> findByUser(User user);
    List<ChallengeParticipation> findByUserAndStatus(User user, ParticipationStatus status);
    
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.user = :user " +
           "ORDER BY p.startedAt DESC")
    Page<ChallengeParticipation> findByUserOrderByJoinedAtDesc(@Param("user") User user, 
                                                                      Pageable pageable);

    // Challenge queries
    List<ChallengeParticipation> findByChallenge(Challenge challenge);
    List<ChallengeParticipation> findByChallengeAndStatus(Challenge challenge, ParticipationStatus status);
    
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "ORDER BY p.progressPercentage DESC, p.startedAt ASC")
    List<ChallengeParticipation> findByChallengeOrderByProgress(@Param("challenge") Challenge challenge);

    // Status queries
    List<ChallengeParticipation> findByStatus(ParticipationStatus status);
    
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.status = 'ACTIVE'")
    List<ChallengeParticipation> findActiveParticipations();

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.status = 'COMPLETED'")
    List<ChallengeParticipation> findCompletedParticipations();

    // Time-based queries
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.startedAt >= :since")
    List<ChallengeParticipation> findParticipationsAfter(@Param("since") LocalDateTime since);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.completedAt >= :since")
    List<ChallengeParticipation> findCompletionsAfter(@Param("since") LocalDateTime since);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.status = 'ACTIVE' " +
           "AND p.lastActivityAt < :threshold")
    List<ChallengeParticipation> findInactiveParticipations(@Param("threshold") LocalDateTime threshold);

    // Statistics queries
    @Query("SELECT COUNT(p) FROM ChallengeParticipation p WHERE p.challenge = :challenge")
    long countByChallengeId(@Param("challenge") Challenge challenge);

    @Query("SELECT COUNT(p) FROM ChallengeParticipation p WHERE p.challenge = :challenge AND p.status = :status")
    long countByChallengeAndStatus(@Param("challenge") Challenge challenge, @Param("status") ParticipationStatus status);

    @Query("SELECT COUNT(p) FROM ChallengeParticipation p WHERE p.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(p) FROM ChallengeParticipation p WHERE p.user = :user AND p.status = 'COMPLETED'")
    long countCompletedByUser(@Param("user") User user);

    // Progress queries
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.progressPercentage >= :minProgress " +
           "AND p.status = 'ACTIVE'")
    List<ChallengeParticipation> findByMinimumProgress(@Param("minProgress") int minProgress);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "AND p.progressPercentage >= :minProgress " +
           "ORDER BY p.progressPercentage DESC")
    List<ChallengeParticipation> findTopPerformersByChallenge(@Param("challenge") Challenge challenge,
                                                             @Param("minProgress") int minProgress);

    // Leaderboard queries
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "AND p.status IN ('ACTIVE', 'COMPLETED') " +
           "ORDER BY p.progressPercentage DESC, p.startedAt ASC")
    List<ChallengeParticipation> findLeaderboardByChallenge(@Param("challenge") Challenge challenge, 
                                                           Pageable pageable);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.status = 'COMPLETED' " +
           "ORDER BY p.completedAt ASC")
    List<ChallengeParticipation> findEarliestCompletions(Pageable pageable);

    // Evidence related queries
    @Query("SELECT p FROM ChallengeParticipation p WHERE SIZE(p.evidences) > 0 " +
           "AND p.status = 'ACTIVE'")
    List<ChallengeParticipation> findParticipationsWithEvidence();

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "AND SIZE(p.evidences) >= :minEvidence")
    List<ChallengeParticipation> findByChallengeWithMinEvidence(@Param("challenge") Challenge challenge,
                                                               @Param("minEvidence") int minEvidence);

    // Business logic queries
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.user = :user " +
           "AND p.status = 'ACTIVE' " +
           "AND p.challenge.status = 'ACTIVE'")
    List<ChallengeParticipation> findActiveParticipationsByUser(@Param("user") User user);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "AND p.status = 'ACTIVE' " +
           "AND p.lastActivityAt >= :recentThreshold " +
           "ORDER BY p.lastActivityAt DESC")
    List<ChallengeParticipation> findRecentlyActiveParticipations(@Param("challenge") Challenge challenge,
                                                                 @Param("recentThreshold") LocalDateTime recentThreshold);

    // Analytics queries
    @Query("SELECT p.status, COUNT(p) FROM ChallengeParticipation p WHERE p.challenge = :challenge GROUP BY p.status")
    List<Object[]> getParticipationStatusStatsByChallenge(@Param("challenge") Challenge challenge);

    @Query("SELECT AVG(p.progressPercentage) FROM ChallengeParticipation p WHERE p.challenge = :challenge " +
           "AND p.status IN ('ACTIVE', 'COMPLETED')")
    Double getAverageProgressByChallenge(@Param("challenge") Challenge challenge);

    @Query("SELECT p FROM ChallengeParticipation p WHERE p.user = :user " +
           "AND p.status = 'COMPLETED' " +
           "ORDER BY p.completedAt DESC")
    List<ChallengeParticipation> findRecentCompletionsByUser(@Param("user") User user, 
                                                            Pageable pageable);
}
