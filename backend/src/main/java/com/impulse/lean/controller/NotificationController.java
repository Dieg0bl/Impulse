package com.impulse.lean.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.domain.model.Notification;
import com.impulse.lean.domain.model.NotificationPriority;
import com.impulse.lean.domain.model.NotificationStatus;
import com.impulse.lean.domain.model.NotificationType;
import com.impulse.lean.service.NotificationService;

/**
 * IMPULSE LEAN v1 - Notification REST Controller
 * 
 * Provides RESTful endpoints for notification operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Create a new notification
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long recipientId,
            @RequestParam NotificationType type,
            @RequestParam String message,
            @RequestParam(defaultValue = "NORMAL") NotificationPriority priority) {
        
        try {
            Notification notification = notificationService.createNotification(
                recipientId, type, message, priority);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Schedule a notification
     */
    @PostMapping("/schedule")
    public ResponseEntity<Notification> scheduleNotification(
            @RequestParam Long recipientId,
            @RequestParam NotificationType type,
            @RequestParam String message,
            @RequestParam(defaultValue = "NORMAL") NotificationPriority priority,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledFor) {
        
        try {
            Notification notification = notificationService.scheduleNotification(
                recipientId, type, message, priority, scheduledFor);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Send notification immediately
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<Map<String, Boolean>> sendNotification(@PathVariable Long id) {
        boolean sent = notificationService.sendNotification(id);
        return ResponseEntity.ok(Map.of("sent", sent));
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        boolean marked = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(Map.of("marked", marked));
    }

    /**
     * Mark multiple notifications as read
     */
    @PutMapping("/read-multiple")
    public ResponseEntity<Map<String, Integer>> markMultipleAsRead(
            @RequestParam List<Long> notificationIds,
            @RequestParam Long userId) {
        
        int markedCount = notificationService.markMultipleAsRead(notificationIds, userId);
        return ResponseEntity.ok(Map.of("markedCount", markedCount));
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get notifications for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Notification>> getNotificationsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsForUser(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notification count for a user
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * Get notifications by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Notification>> getNotificationsByPriority(@PathVariable NotificationPriority priority) {
        List<Notification> notifications = notificationService.getNotificationsByPriority(priority);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Delete old notifications
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Integer>> deleteOldNotifications(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime olderThan) {
        
        int deletedCount = notificationService.deleteOldNotifications(olderThan);
        return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
    }

    /**
     * Process scheduled notifications
     */
    @PostMapping("/process-scheduled")
    public ResponseEntity<Map<String, Integer>> processScheduledNotifications() {
        int processedCount = notificationService.processScheduledNotifications();
        return ResponseEntity.ok(Map.of("processedCount", processedCount));
    }

    /**
     * Retry failed notifications
     */
    @PostMapping("/retry-failed")
    public ResponseEntity<Map<String, Integer>> retryFailedNotifications() {
        int retriedCount = notificationService.retryFailedNotifications();
        return ResponseEntity.ok(Map.of("retriedCount", retriedCount));
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Object> stats = notificationService.getNotificationStats(start, end);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get user notification preferences
     */
    @GetMapping("/user/{userId}/preferences")
    public ResponseEntity<Map<String, Object>> getUserNotificationPreferences(@PathVariable Long userId) {
        Map<String, Object> preferences = notificationService.getUserNotificationPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Update user notification preferences
     */
    @PutMapping("/user/{userId}/preferences")
    public ResponseEntity<Map<String, Boolean>> updateUserNotificationPreferences(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> preferences) {
        
        boolean updated = notificationService.updateUserNotificationPreferences(userId, preferences);
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    /**
     * Send bulk notifications
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Integer>> sendBulkNotifications(
            @RequestParam List<Long> userIds,
            @RequestParam NotificationType type,
            @RequestParam String message) {
        
        int sentCount = notificationService.sendBulkNotifications(userIds, type, message);
        return ResponseEntity.ok(Map.of("sentCount", sentCount));
    }

    /**
     * Cancel scheduled notification
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancelScheduledNotification(@PathVariable Long id) {
        boolean cancelled = notificationService.cancelScheduledNotification(id);
        return ResponseEntity.ok(Map.of("cancelled", cancelled));
    }

    /**
     * Get delivery report
     */
    @GetMapping("/delivery-report")
    public ResponseEntity<Map<String, Object>> getDeliveryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Object> report = notificationService.getDeliveryReport(start, end);
        return ResponseEntity.ok(report);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "NotificationService",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Dashboard summary endpoint
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Object> summary = notificationService.getNotificationStats(weekAgo, now);
        Map<String, Object> deliveryReport = notificationService.getDeliveryReport(weekAgo, now);
        
        summary.put("deliveryReport", deliveryReport);
        
        return ResponseEntity.ok(summary);
    }
}
