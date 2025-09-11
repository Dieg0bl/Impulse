package com.impulse.lean.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.impulse.lean.dto.ContentModerationRequestDto;

/**
 * IMPULSE LEAN v1 - Content Moderation Service Interface
 * 
 * Defines content moderation and filtering operations
 * Provides automated and manual content review capabilities
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface ContentModerationService {

    /**
     * Submit content for moderation
     */
    Map<String, Object> submitForModeration(ContentModerationRequestDto request);

    /**
     * Get moderation status for content
     */
    Map<String, Object> getModerationStatus(String contentId);

    /**
     * Approve content manually
     */
    Map<String, Object> approveContent(String contentId, String moderatorId, String reason);

    /**
     * Reject content with reason
     */
    Map<String, Object> rejectContent(String contentId, String moderatorId, String reason);

    /**
     * Flag content for review
     */
    Map<String, Object> flagContent(String contentId, String flagType, String reason, String reporterId);

    /**
     * Get pending moderation queue
     */
    List<Map<String, Object>> getPendingModerationQueue(String moderatorId, int limit);

    /**
     * Get moderation history for content
     */
    List<Map<String, Object>> getModerationHistory(String contentId);

    /**
     * Auto-moderate content using AI
     */
    Map<String, Object> autoModerateContent(String contentId, String content, String contentType);

    /**
     * Get content moderation statistics
     */
    Map<String, Object> getModerationStats(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Update moderation rules
     */
    Map<String, Object> updateModerationRules(Map<String, Object> rules);

    /**
     * Get active moderation rules
     */
    Map<String, Object> getModerationRules();

    /**
     * Escalate content to senior moderator
     */
    Map<String, Object> escalateContent(String contentId, String moderatorId, String reason);

    /**
     * Get moderator performance metrics
     */
    Map<String, Object> getModeratorMetrics(String moderatorId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Bulk moderate content
     */
    Map<String, Object> bulkModerateContent(List<String> contentIds, String action, String moderatorId);

    /**
     * Get content safety score
     */
    Map<String, Object> getContentSafetyScore(String content, String contentType);

    /**
     * Report false positive/negative
     */
    Map<String, Object> reportModerationError(String contentId, String errorType, String description);

    /**
     * Get trending moderation issues
     */
    List<Map<String, Object>> getTrendingModerationIssues(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Create moderation report
     */
    Map<String, Object> createModerationReport(String reportType, Map<String, Object> parameters);

    /**
     * Get content risk assessment
     */
    Map<String, Object> assessContentRisk(String content, String contentType, Map<String, Object> context);

    /**
     * Enable/disable auto-moderation
     */
    Map<String, Object> toggleAutoModeration(String contentType, boolean enabled);

    /**
     * Get moderation queue by priority
     */
    List<Map<String, Object>> getModerationQueueByPriority(String priority, int limit);

    /**
     * Update content sensitivity level
     */
    Map<String, Object> updateContentSensitivity(String contentId, String sensitivityLevel);

    /**
     * Get moderation appeals
     */
    List<Map<String, Object>> getModerationAppeals(String status, int limit);

    /**
     * Process moderation appeal
     */
    Map<String, Object> processModerationAppeal(String appealId, String decision, String moderatorId, String reason);
}
