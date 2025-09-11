package com.impulse.challenge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.challenge.dto.ChallengeCreateRequestDto;
import com.impulse.challenge.dto.ChallengeJoinRequestDto;
import com.impulse.challenge.dto.ChallengeUpdateRequestDto;
import com.impulse.challenge.model.Challenge;
import com.impulse.challenge.model.ChallengeCategory;
import com.impulse.challenge.model.ChallengeDifficulty;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.ValidationMethod;

/**
 * IMPULSE LEAN v1 - Challenge Service Interface
 * 
 * Business logic interface for challenge operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface ChallengeService {

    // Basic CRUD operations
    Optional<Challenge> findById(Long id);
    Optional<Challenge> findByUuid(String uuid);
    Challenge save(Challenge challenge);
    void deleteById(Long id);

    // Challenge lifecycle
    Challenge createChallenge(User creator, ChallengeCreateRequestDto request);
    Challenge updateChallenge(String uuid, ChallengeUpdateRequestDto request);
    Challenge publishChallenge(String uuid);
    Challenge archiveChallenge(String uuid);
    Challenge deleteChallenge(String uuid);

    // Participation management
    ChallengeParticipation joinChallenge(String challengeUuid, String userUuid, ChallengeJoinRequestDto request);
    ChallengeParticipation leaveChallenge(String challengeUuid, String userUuid);
    boolean canUserJoinChallenge(String challengeUuid, String userUuid);
    boolean isUserParticipating(String challengeUuid, String userUuid);

    // Challenge queries
    List<Challenge> findActiveChallenges();
    List<Challenge> findChallengesByCategory(ChallengeCategory category);
    List<Challenge> findChallengesByDifficulty(ChallengeDifficulty difficulty);
    List<Challenge> findChallengesByCreator(String creatorUuid);
    List<Challenge> findFeaturedChallenges();
    Page<Challenge> findChallenges(Pageable pageable);
    Page<Challenge> searchChallenges(String searchTerm, Pageable pageable);

    // Challenge validation
    boolean isChallengeActive(String uuid);
    boolean isChallengeExpired(String uuid);
    boolean isChallengeAccessible(String uuid, String userUuid);
    boolean canChallengeBeModified(String uuid, String userUuid);

    // Progress tracking
    void updateParticipationProgress(String participationUuid, int progress);
    void completeParticipation(String participationUuid);
    List<ChallengeParticipation> getParticipationsByUser(String userUuid);
    List<ChallengeParticipation> getParticipationsByChallenge(String challengeUuid);

    // Leaderboard and rankings
    List<ChallengeParticipation> getChallengeLeaderboard(String challengeUuid, int limit);
    List<User> getTopParticipants(String challengeUuid);
    double getAverageCompletionRate(String challengeUuid);
    long getTotalParticipants(String challengeUuid);

    // Statistics and analytics
    long getTotalChallengeCount();
    long getActiveChallengeCount();
    long getChallengeCountByCategory(ChallengeCategory category);
    long getChallengeCountByDifficulty(ChallengeDifficulty difficulty);
    List<Challenge> getMostPopularChallenges(int limit);
    List<Challenge> getRecentChallenges(int limit);

    // Content management
    Challenge updateChallengeContent(String uuid, String requirements, String guidelines);
    Challenge updateChallengeSettings(String uuid, int maxParticipants, 
                                     LocalDateTime startDate, LocalDateTime endDate);
    Challenge updateChallengeValidation(String uuid, ValidationMethod validationMethod);

    // Bulk operations
    List<Challenge> findExpiredChallenges();
    void archiveExpiredChallenges();
    void cleanupInactiveChallenges(LocalDateTime threshold);

    // Recommendations
    List<Challenge> getRecommendedChallenges(String userUuid);
    List<Challenge> getSimilarChallenges(String challengeUuid);
    List<Challenge> getChallengesByUserInterests(String userUuid);
}
