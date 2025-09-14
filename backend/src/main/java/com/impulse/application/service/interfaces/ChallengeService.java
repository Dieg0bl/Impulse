package com.impulse.application.service.interfaces;

import com.impulse.application.dto.challenge.*;
import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeDifficulty;

import java.util.List;

/**
 * Interface for Challenge Service operations
 */
public interface ChallengeService {

    /**
     * Create a new challenge
     */
    ChallengeResponseDto createChallenge(Long userId, ChallengeCreateRequestDto request);

    /**
     * Update an existing challenge
     */
    ChallengeResponseDto updateChallenge(Long challengeId, Long userId, ChallengeUpdateRequestDto request);

    /**
     * Get challenge by ID
     */
    ChallengeResponseDto getChallengeById(Long id);

    /**
     * Get challenge by UUID
     */
    ChallengeResponseDto getChallengeByUuid(String uuid);

    /**
     * Get all challenges with pagination
     */
    PaginationResponse<ChallengeResponseDto> getAllChallenges(PaginationRequest request);

    /**
     * Get challenges by category
     */
    PaginationResponse<ChallengeResponseDto> getChallengesByCategory(
        ChallengeCategory category, PaginationRequest request);

    /**
     * Get challenges by difficulty
     */
    PaginationResponse<ChallengeResponseDto> getChallengesByDifficulty(
        ChallengeDifficulty difficulty, PaginationRequest request);

    /**
     * Get challenges by creator
     */
    PaginationResponse<ChallengeResponseDto> getChallengesByCreator(
        Long creatorId, PaginationRequest request);

    /**
     * Join a challenge
     */
    void joinChallenge(Long userId, ChallengeJoinRequestDto request);

    /**
     * Leave a challenge
     */
    void leaveChallenge(Long userId, Long challengeId);

    /**
     * Delete a challenge
     */
    void deleteChallenge(Long challengeId, Long userId);

    /**
     * Check if user can join challenge
     */
    boolean canUserJoinChallenge(Long userId, Long challengeId);

    /**
     * Check if user is participant of challenge
     */
    boolean isUserParticipant(Long userId, Long challengeId);

    /**
     * Get active challenges for user
     */
    List<ChallengeResponseDto> getActiveChallengesForUser(Long userId);

    /**
     * Search challenges by title or description
     */
    PaginationResponse<ChallengeResponseDto> searchChallenges(String query, PaginationRequest request);
}
