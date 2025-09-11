package com.impulse.lean.service;

import com.impulse.lean.domain.model.*;
import com.impulse.lean.repository.AchievementRepository;
import com.impulse.lean.repository.UserAchievementRepository;
import com.impulse.lean.dto.response.AchievementResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IMPULSE LEAN v1 - Achievement Service
 * 
 * Service for achievement management and operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class AchievementService {

    private static final Logger logger = LoggerFactory.getLogger(AchievementService.class);

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    /**
     * Get all active achievements
     */
    public List<Achievement> getAllActiveAchievements() {
        return achievementRepository.findActiveAchievements(AchievementStatus.ACTIVE);
    }

    /**
     * Get achievements by category
     */
    public List<Achievement> getAchievementsByCategory(AchievementCategory category) {
        return achievementRepository.findAvailableByCategory(category);
    }

    /**
     * Get achievement by code
     */
    public Optional<Achievement> getAchievementByCode(String code) {
        return achievementRepository.findByCode(code);
    }

    /**
     * Get achievements by point range
     */
    public List<Achievement> getAchievementsByPointRange(Integer minPoints, Integer maxPoints) {
        return achievementRepository.findByPointsRange(minPoints, maxPoints);
    }

    /**
     * Search achievements by text
     */
    public List<Achievement> searchAchievements(String searchTerm) {
        return achievementRepository.searchAchievements(searchTerm);
    }

    /**
     * Get achievements with badges
     */
    public List<Achievement> getAchievementsWithBadges() {
        return achievementRepository.findAchievementsWithBadges();
    }

    /**
     * Get available achievement categories
     */
    public List<AchievementCategory> getAvailableCategories() {
        return achievementRepository.findAvailableCategories();
    }

    /**
     * Get achievements by levels
     */
    public List<Achievement> getAchievementsByLevels(List<AchievementLevel> levels) {
        return achievementRepository.findByLevels(levels);
    }

    /**
     * Get achievements eligible for user based on current stats
     */
    public List<Achievement> getEligibleAchievements(String criteriaType, Integer currentValue) {
        return achievementRepository.findEligibleAchievements(criteriaType, currentValue);
    }

    /**
     * Create new achievement
     */
    public Achievement createAchievement(Achievement achievement) {
        try {
            // Validate unique code
            if (achievementRepository.existsByCode(achievement.getCode())) {
                throw new IllegalArgumentException("Achievement code already exists: " + achievement.getCode());
            }

            achievement.setCreatedAt(LocalDateTime.now());
            achievement.setStatus(AchievementStatus.ACTIVE);
            
            Achievement saved = achievementRepository.save(achievement);
            logger.info("Created new achievement: {}", saved.getCode());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("Error creating achievement {}: {}", achievement.getCode(), e.getMessage());
            throw new RuntimeException("Failed to create achievement", e);
        }
    }

    /**
     * Update achievement
     */
    public Achievement updateAchievement(Long id, Achievement achievementData) {
        try {
            Optional<Achievement> existing = achievementRepository.findById(id);
            if (existing.isEmpty()) {
                throw new IllegalArgumentException("Achievement not found: " + id);
            }

            Achievement achievement = existing.get();
            
            // Update fields
            achievement.setTitle(achievementData.getTitle());
            achievement.setDescription(achievementData.getDescription());
            achievement.setPoints(achievementData.getPoints());
            achievement.setLevel(achievementData.getLevel());
            achievement.setCategory(achievementData.getCategory());
            achievement.setBadgeIcon(achievementData.getBadgeIcon() != null ? achievementData.getBadgeIcon() : "default.png");
            achievement.setIsRepeatable(achievementData.getIsRepeatable());
            achievement.setIsSecret(achievementData.getIsSecret());
            achievement.setUpdatedAt(LocalDateTime.now());

            Achievement saved = achievementRepository.save(achievement);
            logger.info("Updated achievement: {}", saved.getCode());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("Error updating achievement {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update achievement", e);
        }
    }

    /**
     * Deactivate achievement
     */
    public void deactivateAchievement(Long id) {
        try {
            Optional<Achievement> existing = achievementRepository.findById(id);
            if (existing.isEmpty()) {
                throw new IllegalArgumentException("Achievement not found: " + id);
            }

            Achievement achievement = existing.get();
            achievement.setStatus(AchievementStatus.DISABLED);
            achievement.setActive(false);
            achievement.setUpdatedAt(LocalDateTime.now());

            achievementRepository.save(achievement);
            logger.info("Deactivated achievement: {}", achievement.getCode());
            
        } catch (Exception e) {
            logger.error("Error deactivating achievement {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to deactivate achievement", e);
        }
    }

    /**
     * Get achievement statistics
     */
    public Object getAchievementStatistics() {
        try {
            Long totalActive = achievementRepository.countByStatus(AchievementStatus.ACTIVE);
            Long totalDisabled = achievementRepository.countByStatus(AchievementStatus.DISABLED);
            
            return new Object() {
                public final Long activeAchievements = totalActive;
                public final Long disabledAchievements = totalDisabled;
                public final Long totalAchievements = totalActive + totalDisabled;
            };
            
        } catch (Exception e) {
            logger.error("Error getting achievement statistics: {}", e.getMessage());
            throw new RuntimeException("Failed to get achievement statistics", e);
        }
    }

    /**
     * Get category statistics
     */
    public List<Object> getCategoryStatistics() {
        try {
            return List.of(AchievementCategory.values()).stream()
                    .map(category -> {
                        Long count = achievementRepository.countActiveByCategory(category);
                        Double avgPoints = achievementRepository.getAveragePointsByCategory(category);
                        
                        return new Object() {
                            public final AchievementCategory category = category;
                            public final Long achievementCount = count;
                            public final Double averagePoints = avgPoints != null ? avgPoints : 0.0;
                        };
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error getting category statistics: {}", e.getMessage());
            throw new RuntimeException("Failed to get category statistics", e);
        }
    }

    /**
     * Convert Achievement to AchievementResponse DTO
     */
    public AchievementResponse convertToResponse(Achievement achievement) {
        return new AchievementResponse(
            achievement.getId(),
            achievement.getCode(),
            achievement.getTitle(),
            achievement.getDescription(),
            achievement.getCategory(),
            achievement.getLevel(),
            achievement.getPoints(),
            null, // earnedAt - only for user achievements
            null  // earnedCount - only for user achievements
        );
    }

    /**
     * Convert list of achievements to response DTOs
     */
    public List<AchievementResponse> convertToResponseList(List<Achievement> achievements) {
        return achievements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated achievements
     */
    public Page<Achievement> getAchievementsPaginated(Pageable pageable) {
        return achievementRepository.findByStatusOrderByLevelAscPointsAsc(AchievementStatus.ACTIVE, pageable);
    }

    /**
     * Bulk create achievements
     */
    public List<Achievement> bulkCreateAchievements(List<Achievement> achievements) {
        try {
            // Validate unique codes
            for (Achievement achievement : achievements) {
                if (achievementRepository.existsByCode(achievement.getCode())) {
                    throw new IllegalArgumentException("Achievement code already exists: " + achievement.getCode());
                }
                achievement.setCreatedAt(LocalDateTime.now());
                achievement.setStatus(AchievementStatus.ACTIVE);
            }

            List<Achievement> saved = achievementRepository.saveAll(achievements);
            logger.info("Bulk created {} achievements", saved.size());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("Error bulk creating achievements: {}", e.getMessage());
            throw new RuntimeException("Failed to bulk create achievements", e);
        }
    }
}
