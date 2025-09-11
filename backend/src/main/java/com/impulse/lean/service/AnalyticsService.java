package com.impulse.lean.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IMPULSE LEAN v1 - Analytics Service Interface
 * 
 * Service interface for analytics and reporting operations
 * Provides insights into system usage, performance and business metrics
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface AnalyticsService {

    /**
     * Get dashboard summary metrics
     */
    Map<String, Object> getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get user engagement analytics
     */
    Map<String, Object> getUserEngagementMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get evidence submission analytics
     */
    Map<String, Object> getEvidenceAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get validation performance metrics
     */
    Map<String, Object> getValidationMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get challenge participation analytics
     */
    Map<String, Object> getChallengeAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get top performing users
     */
    List<Map<String, Object>> getTopPerformers(int limit, String metric, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get system usage statistics
     */
    Map<String, Object> getSystemUsageStats(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get quality metrics
     */
    Map<String, Object> getQualityMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get growth analytics
     */
    Map<String, Object> getGrowthMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get real-time metrics
     */
    Map<String, Object> getRealTimeMetrics();

    /**
     * Get user activity timeline
     */
    List<Map<String, Object>> getUserActivityTimeline(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get platform performance metrics
     */
    Map<String, Object> getPlatformMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get content analytics
     */
    Map<String, Object> getContentAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get notification analytics
     */
    Map<String, Object> getNotificationAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate custom report
     */
    Map<String, Object> generateCustomReport(String reportType, Map<String, Object> parameters);

    /**
     * Get trending topics/challenges
     */
    List<Map<String, Object>> getTrendingTopics(int limit);

    /**
     * Get geographic distribution analytics
     */
    Map<String, Object> getGeographicAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get device/platform usage analytics
     */
    Map<String, Object> getDeviceAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get retention analytics
     */
    Map<String, Object> getRetentionMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Export analytics data
     */
    byte[] exportAnalyticsData(String format, String reportType, Map<String, Object> parameters);

    /**
     * Get predictive analytics
     */
    Map<String, Object> getPredictiveAnalytics(String predictionType, Map<String, Object> parameters);

    /**
     * Get A/B testing results
     */
    Map<String, Object> getABTestResults(String testId);

    /**
     * Get conversion funnel analytics
     */
    Map<String, Object> getConversionFunnelMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get cohort analysis
     */
    Map<String, Object> getCohortAnalysis(String cohortType, LocalDateTime startDate, LocalDateTime endDate);
}
