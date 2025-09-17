package com.impulse.infrastructure.services;

import com.impulse.application.dto.challenge.*;
import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.application.service.interfaces.ChallengeService;
import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeDifficulty;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.user.User;
import com.impulse.infrastructure.persistence.repositories.ChallengeRepository;
import com.impulse.infrastructure.persistence.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ChallengeService interface
 */
@Service
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChallengeServiceImpl(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChallengeResponseDto createChallenge(Long userId, ChallengeCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Challenge challenge = new Challenge();
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setType(ChallengeType.valueOf(request.getType()));
        challenge.setStatus(ChallengeStatus.DRAFT);
        challenge.setDifficulty(DifficultyLevel.valueOf(request.getDifficulty()));
        challenge.setPointsReward(request.getPointsReward());
        challenge.setMonetaryReward(request.getMonetaryReward());
        challenge.setStartDate(request.getStartDate());
        challenge.setEndDate(request.getEndDate());
        challenge.setMaxParticipants(request.getMaxParticipants());
        challenge.setUser(user);

        Challenge savedChallenge = challengeRepository.save(challenge);
        return mapToResponseDto(savedChallenge);
    }

    @Override
    public ChallengeResponseDto updateChallenge(Long challengeId, Long userId, ChallengeUpdateRequestDto request) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));

        // Verify ownership
        if (!challenge.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this challenge");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            challenge.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            challenge.setDescription(request.getDescription());
        }
        if (request.getDifficulty() != null) {
            challenge.setDifficulty(DifficultyLevel.valueOf(request.getDifficulty()));
        }
        if (request.getPointsReward() != null) {
            challenge.setPointsReward(request.getPointsReward());
        }
        if (request.getMonetaryReward() != null) {
            challenge.setMonetaryReward(request.getMonetaryReward());
        }
        if (request.getStartDate() != null) {
            challenge.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            challenge.setEndDate(request.getEndDate());
        }

        Challenge updatedChallenge = challengeRepository.save(challenge);
        return mapToResponseDto(updatedChallenge);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeResponseDto getChallengeById(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + id));
        return mapToResponseDto(challenge);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeResponseDto getChallengeByUuid(String uuid) {
        try {
            Long id = Long.parseLong(uuid);
            return getChallengeById(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid UUID format: " + uuid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengeResponseDto> getAllChallenges(PaginationRequest request) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(request.getSortDirection()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(direction, request.getSortBy())
        );

        Page<Challenge> challengePage = challengeRepository.findAll(pageable);
        List<ChallengeResponseDto> challenges = challengePage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(challenges, challengePage.getNumber(), challengePage.getSize(), challengePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengeResponseDto> getChallengesByCategory(
            ChallengeCategory category, PaginationRequest request) {
        // For now, return all challenges since we don't have category filtering implemented
        return getAllChallenges(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengeResponseDto> getChallengesByDifficulty(
            ChallengeDifficulty difficulty, PaginationRequest request) {
        // For now, return all challenges since we don't have difficulty filtering implemented
        return getAllChallenges(request);
    }

    @Override
    public void deleteChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));

        // Verify ownership
        if (!challenge.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this challenge");
        }

        challengeRepository.deleteById(challengeId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengeResponseDto> getChallengesByUser(
            Long userId, PaginationRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Challenge> challengePage = challengeRepository.findByUserId(userId, pageable);

        List<ChallengeResponseDto> challenges = challengePage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(challenges, challengePage.getNumber(), challengePage.getSize(), challengePage.getTotalElements());
    }

    @Override
    public ChallengeResponseDto activateChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));

        // Verify ownership
        if (!challenge.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to activate this challenge");
        }

        challenge.setStatus(ChallengeStatus.ACTIVE);
        Challenge updatedChallenge = challengeRepository.save(challenge);
        return mapToResponseDto(updatedChallenge);
    }

    @Override
    public ChallengeResponseDto completeChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));

        // Verify ownership
        if (!challenge.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to complete this challenge");
        }

        challenge.setStatus(ChallengeStatus.COMPLETED);
        Challenge updatedChallenge = challengeRepository.save(challenge);
        return mapToResponseDto(updatedChallenge);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ChallengeResponseDto> searchChallenges(String query, PaginationRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        // Simple implementation for now
        Page<Challenge> challengePage = challengeRepository.findAll(pageable);

        List<ChallengeResponseDto> challenges = challengePage.getContent().stream()
                .filter(challenge -> challenge.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                    (challenge.getDescription() != null &&
                                     challenge.getDescription().toLowerCase().contains(query.toLowerCase())))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(challenges, challengePage.getNumber(), challengePage.getSize(), challengePage.getTotalElements());
    }

    /**
     * Maps Challenge entity to ChallengeResponseDto
     */
    private ChallengeResponseDto mapToResponseDto(Challenge challenge) {
        return new ChallengeResponseDto(
                challenge.getId(),
                challenge.getId().toString(), // uuid
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getType() != null ? challenge.getType().toString() : null,
                challenge.getStatus() != null ? challenge.getStatus().toString() : null,
                challenge.getDifficulty() != null ? challenge.getDifficulty().toString() : null,
                challenge.getPointsReward(),
                challenge.getMonetaryReward(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getMaxParticipants(),
                0, // currentParticipants
                challenge.getUser() != null ? challenge.getUser().getId() : null,
                challenge.getUser() != null ? challenge.getUser().getUsername() : null,
                challenge.getCreatedAt(),
                challenge.getUpdatedAt(),
                false, // isParticipating
                0, // evidenceCount
                null, // category
                null, // tags
                null  // requirements
        );
    }
}

