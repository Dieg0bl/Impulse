package com.impulse.lean.application.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.lean.application.dto.challenge.ChallengeCreateRequestDto;
import com.impulse.lean.application.dto.challenge.ChallengeJoinRequestDto;
import com.impulse.lean.application.dto.challenge.ChallengeUpdateRequestDto;
import com.impulse.lean.application.service.interfaces.ChallengeService;
import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeCategory;
import com.impulse.lean.domain.model.ChallengeDifficulty;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.ChallengeStatus;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.ValidationMethod;
import com.impulse.lean.domain.repository.ChallengeRepository;

/**
 * IMPULSE LEAN v1 - Challenge Service Implementation
 * 
 * Business logic implementation for challenge operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Autowired
    public ChallengeServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    // ========== BASIC CRUD OPERATIONS ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<Challenge> findById(Long id) {
        return challengeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Challenge> findByUuid(String uuid) {
        return challengeRepository.findByUuid(uuid);
    }

    @Override
    public Challenge save(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    @Override
    public void deleteById(Long id) {
        challengeRepository.deleteById(id);
    }

    // ========== CHALLENGE LIFECYCLE ==========

    @Override
    public Challenge createChallenge(User creator, ChallengeCreateRequestDto request) {
        Challenge challenge = new Challenge();
        challenge.setUuid(UUID.randomUUID().toString());
        challenge.setCreator(creator);
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        
        // Convert strings to enums
        try {
            challenge.setCategory(ChallengeCategory.valueOf(request.getCategory().toUpperCase()));
            challenge.setDifficulty(ChallengeDifficulty.valueOf(request.getDifficulty().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category or difficulty value");
        }
        
        challenge.setStartDate(request.getStartDate());
        challenge.setEndDate(request.getEndDate());
        challenge.setMaxParticipants(request.getMaxParticipants());
        challenge.setRules(request.getRequirements());
        challenge.setValidationCriteria(request.getGuidelines());
        challenge.setFeatured(false); // Default value since not in DTO
        challenge.setStatus(ChallengeStatus.DRAFT);
        
        // Generate slug from title
        String slug = generateSlug(request.getTitle());
        challenge.setSlug(slug);
        
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge updateChallenge(String uuid, ChallengeUpdateRequestDto request) {
        Challenge challenge = challengeRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // Update only provided fields
        if (request.getTitle() != null) {
            challenge.setTitle(request.getTitle());
            challenge.setSlug(generateSlug(request.getTitle()));
        }
        if (request.getDescription() != null) {
            challenge.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            challenge.setCategory(ChallengeCategory.valueOf(request.getCategory().toUpperCase()));
        }
        if (request.getDifficulty() != null) {
            challenge.setDifficulty(ChallengeDifficulty.valueOf(request.getDifficulty().toUpperCase()));
        }
        if (request.getEndDate() != null) {
            challenge.setEndDate(request.getEndDate());
        }
        if (request.getMaxParticipants() != null) {
            challenge.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getRequirements() != null) {
            challenge.setRules(request.getRequirements());
        }
        if (request.getGuidelines() != null) {
            challenge.setValidationCriteria(request.getGuidelines());
        }
        
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge publishChallenge(String uuid) {
        Challenge challenge = challengeRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        challenge.setStatus(ChallengeStatus.PUBLISHED);
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge archiveChallenge(String uuid) {
        Challenge challenge = challengeRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        challenge.setStatus(ChallengeStatus.ARCHIVED);
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge deleteChallenge(String uuid) {
        Challenge challenge = challengeRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        challenge.setStatus(ChallengeStatus.DELETED);
        return challengeRepository.save(challenge);
    }

    // ========== PARTICIPATION MANAGEMENT ==========

    @Override
    public ChallengeParticipation joinChallenge(String challengeUuid, String userUuid, ChallengeJoinRequestDto request) {
        // TODO: Implement participation logic
        throw new UnsupportedOperationException("Challenge participation not yet implemented");
    }

    @Override
    public ChallengeParticipation leaveChallenge(String challengeUuid, String userUuid) {
        // TODO: Implement leave logic
        throw new UnsupportedOperationException("Challenge participation not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserJoinChallenge(String challengeUuid, String userUuid) {
        Optional<Challenge> challengeOpt = challengeRepository.findByUuid(challengeUuid);
        if (challengeOpt.isEmpty()) {
            return false;
        }
        
        Challenge challenge = challengeOpt.get();
        return challenge.getStatus() == ChallengeStatus.PUBLISHED;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserParticipating(String challengeUuid, String userUuid) {
        // TODO: Implement participation check
        return false;
    }

    // ========== CHALLENGE QUERIES ==========

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> findActiveChallenges() {
        return challengeRepository.findActiveChallenges();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> findChallengesByCategory(ChallengeCategory category) {
        return challengeRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> findChallengesByDifficulty(ChallengeDifficulty difficulty) {
        return challengeRepository.findByDifficulty(difficulty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> findChallengesByCreator(String creatorUuid) {
        // TODO: Implement creator lookup
        throw new UnsupportedOperationException("Creator lookup not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> findFeaturedChallenges() {
        return challengeRepository.findFeaturedChallenges();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Challenge> findChallenges(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Challenge> searchChallenges(String searchTerm, Pageable pageable) {
        return challengeRepository.searchChallenges(searchTerm, pageable);
    }

    // ========== CHALLENGE VALIDATION ==========

    @Override
    @Transactional(readOnly = true)
    public boolean isChallengeActive(String uuid) {
        Optional<Challenge> challengeOpt = challengeRepository.findByUuid(uuid);
        return challengeOpt.map(challenge -> 
            challenge.getStatus() == ChallengeStatus.PUBLISHED
        ).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isChallengeExpired(String uuid) {
        Optional<Challenge> challengeOpt = challengeRepository.findByUuid(uuid);
        return challengeOpt.map(challenge -> 
            challenge.getEndDate() != null && 
            challenge.getEndDate().isBefore(LocalDateTime.now())
        ).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isChallengeAccessible(String uuid, String userUuid) {
        // TODO: Implement access control logic
        return isChallengeActive(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canChallengeBeModified(String uuid, String userUuid) {
        // TODO: Implement modification permission logic
        Optional<Challenge> challengeOpt = challengeRepository.findByUuid(uuid);
        return challengeOpt.map(challenge -> 
            challenge.getStatus() == ChallengeStatus.DRAFT
        ).orElse(false);
    }

    // ========== STUB IMPLEMENTATIONS FOR REMAINING METHODS ==========
    // These will be implemented in subsequent phases

    @Override
    public void updateParticipationProgress(String participationUuid, int progress) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void completeParticipation(String participationUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<ChallengeParticipation> getParticipationsByUser(String userUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<ChallengeParticipation> getParticipationsByChallenge(String challengeUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<ChallengeParticipation> getChallengeLeaderboard(String challengeUuid, int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<User> getTopParticipants(String challengeUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public double getAverageCompletionRate(String challengeUuid) {
        return 0.0;
    }

    @Override
    public long getTotalParticipants(String challengeUuid) {
        return 0;
    }

    @Override
    public long getTotalChallengeCount() {
        return challengeRepository.count();
    }

    @Override
    public long getActiveChallengeCount() {
        return challengeRepository.countByStatus(ChallengeStatus.PUBLISHED);
    }

    @Override
    public long getChallengeCountByCategory(ChallengeCategory category) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long getChallengeCountByDifficulty(ChallengeDifficulty difficulty) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> getMostPopularChallenges(int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> getRecentChallenges(int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Challenge updateChallengeContent(String uuid, String requirements, String guidelines) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Challenge updateChallengeSettings(String uuid, int maxParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Challenge updateChallengeValidation(String uuid, ValidationMethod validationMethod) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> findExpiredChallenges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void archiveExpiredChallenges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void cleanupInactiveChallenges(LocalDateTime threshold) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> getRecommendedChallenges(String userUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> getSimilarChallenges(String challengeUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Challenge> getChallengesByUserInterests(String userUuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // ========== HELPER METHODS ==========

    private String generateSlug(String title) {
        return title.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }
}
