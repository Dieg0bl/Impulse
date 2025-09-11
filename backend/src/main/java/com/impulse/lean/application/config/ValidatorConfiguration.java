package com.impulse.lean.application.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * IMPULSE LEAN v1 - Validator Configuration
 * 
 * Configuration properties for validator management system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "impulse.validator")
public class ValidatorConfiguration {

    // Assignment configuration
    private Duration assignmentTimeout = Duration.ofHours(24);
    private Duration assignmentReminder = Duration.ofHours(6);
    private int maxAssignmentsPerValidator = 10;
    private int autoAssignmentRetries = 3;

    // Capacity configuration
    private int defaultMaxAssignments = 5;
    private int minRequiredValidators = 2;
    private boolean enableAutoAssignment = true;
    private boolean enableWorkloadBalancing = true;

    // Performance thresholds
    private double minAccuracyScore = 0.8;
    private double minRatingForPriority = 4.0;
    private Duration maxResponseTime = Duration.ofHours(48);
    private int minValidationsForRating = 10;

    // Notification configuration
    private boolean enableAssignmentNotifications = true;
    private boolean enableOverdueNotifications = true;
    private boolean enableCapacityWarnings = true;
    private Duration overdueNotificationInterval = Duration.ofHours(6);

    // Validation workflow
    private boolean requireDoubleValidation = true;
    private boolean enableValidatorFeedback = true;
    private boolean enablePerformanceTracking = true;

    // Specialty matching
    private boolean strictSpecialtyMatching = false;
    private int maxSpecialtiesPerValidator = 5;
    private boolean enableSpecialtyRecommendations = true;

    // Getters and setters
    public Duration getAssignmentTimeout() {
        return assignmentTimeout;
    }

    public void setAssignmentTimeout(Duration assignmentTimeout) {
        this.assignmentTimeout = assignmentTimeout;
    }

    public Duration getAssignmentReminder() {
        return assignmentReminder;
    }

    public void setAssignmentReminder(Duration assignmentReminder) {
        this.assignmentReminder = assignmentReminder;
    }

    public int getMaxAssignmentsPerValidator() {
        return maxAssignmentsPerValidator;
    }

    public void setMaxAssignmentsPerValidator(int maxAssignmentsPerValidator) {
        this.maxAssignmentsPerValidator = maxAssignmentsPerValidator;
    }

    public int getAutoAssignmentRetries() {
        return autoAssignmentRetries;
    }

    public void setAutoAssignmentRetries(int autoAssignmentRetries) {
        this.autoAssignmentRetries = autoAssignmentRetries;
    }

    public int getDefaultMaxAssignments() {
        return defaultMaxAssignments;
    }

    public void setDefaultMaxAssignments(int defaultMaxAssignments) {
        this.defaultMaxAssignments = defaultMaxAssignments;
    }

    public int getMinRequiredValidators() {
        return minRequiredValidators;
    }

    public void setMinRequiredValidators(int minRequiredValidators) {
        this.minRequiredValidators = minRequiredValidators;
    }

    public boolean isEnableAutoAssignment() {
        return enableAutoAssignment;
    }

    public void setEnableAutoAssignment(boolean enableAutoAssignment) {
        this.enableAutoAssignment = enableAutoAssignment;
    }

    public boolean isEnableWorkloadBalancing() {
        return enableWorkloadBalancing;
    }

    public void setEnableWorkloadBalancing(boolean enableWorkloadBalancing) {
        this.enableWorkloadBalancing = enableWorkloadBalancing;
    }

    public double getMinAccuracyScore() {
        return minAccuracyScore;
    }

    public void setMinAccuracyScore(double minAccuracyScore) {
        this.minAccuracyScore = minAccuracyScore;
    }

    public double getMinRatingForPriority() {
        return minRatingForPriority;
    }

    public void setMinRatingForPriority(double minRatingForPriority) {
        this.minRatingForPriority = minRatingForPriority;
    }

    public Duration getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(Duration maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public int getMinValidationsForRating() {
        return minValidationsForRating;
    }

    public void setMinValidationsForRating(int minValidationsForRating) {
        this.minValidationsForRating = minValidationsForRating;
    }

    public boolean isEnableAssignmentNotifications() {
        return enableAssignmentNotifications;
    }

    public void setEnableAssignmentNotifications(boolean enableAssignmentNotifications) {
        this.enableAssignmentNotifications = enableAssignmentNotifications;
    }

    public boolean isEnableOverdueNotifications() {
        return enableOverdueNotifications;
    }

    public void setEnableOverdueNotifications(boolean enableOverdueNotifications) {
        this.enableOverdueNotifications = enableOverdueNotifications;
    }

    public boolean isEnableCapacityWarnings() {
        return enableCapacityWarnings;
    }

    public void setEnableCapacityWarnings(boolean enableCapacityWarnings) {
        this.enableCapacityWarnings = enableCapacityWarnings;
    }

    public Duration getOverdueNotificationInterval() {
        return overdueNotificationInterval;
    }

    public void setOverdueNotificationInterval(Duration overdueNotificationInterval) {
        this.overdueNotificationInterval = overdueNotificationInterval;
    }

    public boolean isRequireDoubleValidation() {
        return requireDoubleValidation;
    }

    public void setRequireDoubleValidation(boolean requireDoubleValidation) {
        this.requireDoubleValidation = requireDoubleValidation;
    }

    public boolean isEnableValidatorFeedback() {
        return enableValidatorFeedback;
    }

    public void setEnableValidatorFeedback(boolean enableValidatorFeedback) {
        this.enableValidatorFeedback = enableValidatorFeedback;
    }

    public boolean isEnablePerformanceTracking() {
        return enablePerformanceTracking;
    }

    public void setEnablePerformanceTracking(boolean enablePerformanceTracking) {
        this.enablePerformanceTracking = enablePerformanceTracking;
    }

    public boolean isStrictSpecialtyMatching() {
        return strictSpecialtyMatching;
    }

    public void setStrictSpecialtyMatching(boolean strictSpecialtyMatching) {
        this.strictSpecialtyMatching = strictSpecialtyMatching;
    }

    public int getMaxSpecialtiesPerValidator() {
        return maxSpecialtiesPerValidator;
    }

    public void setMaxSpecialtiesPerValidator(int maxSpecialtiesPerValidator) {
        this.maxSpecialtiesPerValidator = maxSpecialtiesPerValidator;
    }

    public boolean isEnableSpecialtyRecommendations() {
        return enableSpecialtyRecommendations;
    }

    public void setEnableSpecialtyRecommendations(boolean enableSpecialtyRecommendations) {
        this.enableSpecialtyRecommendations = enableSpecialtyRecommendations;
    }
}
