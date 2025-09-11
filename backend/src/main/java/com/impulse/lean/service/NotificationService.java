package com.impulse.lean.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.lean.domain.model.Notification;
import com.impulse.lean.domain.model.NotificationPriority;
import com.impulse.lean.domain.model.NotificationStatus;
import com.impulse.lean.domain.model.NotificationType;

/**
 * IMPULSE LEAN v1 - Notification Service Interface
 * 
 * Service interface for managing system notifications
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface NotificationService {

    /**
     * Create a new notification
     */
    Notification createNotification(Long recipientId, NotificationType type, 
                                  String message, NotificationPriority priority);

    /**
     * Send notification immediately
     */
    boolean sendNotification(Long notificationId);

    /**
     * Schedule notification for later delivery
     */
    Notification scheduleNotification(Long recipientId, NotificationType type, 
                                    String message, NotificationPriority priority, 
                                    LocalDateTime scheduledFor);

    /**
     * Mark notification as read
     */
    boolean markAsRead(Long notificationId, Long userId);

    /**
     * Mark multiple notifications as read
     */
    int markMultipleAsRead(List<Long> notificationIds, Long userId);

    /**
     * Get notification by ID
     */
    Optional<Notification> getNotificationById(Long notificationId);

    /**
     * Get notifications for a user
     */
    Page<Notification> getNotificationsForUser(Long userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * Get unread notification count for a user
     */
    long getUnreadNotificationCount(Long userId);

    /**
     * Get notifications by type
     */
    List<Notification> getNotificationsByType(NotificationType type);

    /**
     * Get notifications by status
     */
    List<Notification> getNotificationsByStatus(NotificationStatus status);

    /**
     * Get notifications by priority
     */
    List<Notification> getNotificationsByPriority(NotificationPriority priority);

    /**
     * Delete notification
     */
    boolean deleteNotification(Long notificationId);

    /**
     * Delete old notifications
     */
    int deleteOldNotifications(LocalDateTime olderThan);

    /**
     * Process scheduled notifications
     */
    int processScheduledNotifications();

    /**
     * Retry failed notifications
     */
    int retryFailedNotifications();

    /**
     * Get notification statistics
     */
    Map<String, Object> getNotificationStats(LocalDateTime start, LocalDateTime end);

    /**
     * Get user notification preferences
     */
    Map<String, Object> getUserNotificationPreferences(Long userId);

    /**
     * Update user notification preferences
     */
    boolean updateUserNotificationPreferences(Long userId, Map<String, Object> preferences);

    /**
     * Send bulk notifications
     */
    int sendBulkNotifications(List<Long> userIds, NotificationType type, String message);

    /**
     * Cancel scheduled notification
     */
    boolean cancelScheduledNotification(Long notificationId);

    /**
     * Get notification delivery report
     */
    Map<String, Object> getDeliveryReport(LocalDateTime start, LocalDateTime end);
}
