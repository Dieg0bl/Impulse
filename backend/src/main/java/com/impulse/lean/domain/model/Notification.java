package com.impulse.lean.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * IMPULSE LEAN v1 - Notification Domain Model
 * 
 * Represents system notifications for users
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_type", columnList = "type"),
    @Index(name = "idx_notifications_status", columnList = "status"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private NotificationPriority priority;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "action_text")
    private String actionText;

    // Related entity references
    @Column(name = "evidence_uuid")
    private String evidenceUuid;

    @Column(name = "validator_uuid")
    private String validatorUuid;

    @Column(name = "validation_uuid")
    private String validationUuid;

    @Column(name = "challenge_uuid")
    private String challengeUuid;

    // Notification timing
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Delivery channels
    @Column(name = "email_sent")
    private boolean emailSent = false;

    @Column(name = "push_sent")
    private boolean pushSent = false;

    @Column(name = "sms_sent")
    private boolean smsSent = false;

    // Metadata
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Notification() {
        this.uuid = UUID.randomUUID().toString();
        this.status = NotificationStatus.PENDING;
        this.priority = NotificationPriority.NORMAL;
    }

    public Notification(User user, NotificationType type, String title, String message) {
        this();
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    // Business methods
    public void markAsRead() {
        if (this.status != NotificationStatus.READ) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    public void markAsSent() {
        if (this.status == NotificationStatus.PENDING) {
            this.status = NotificationStatus.SENT;
            this.sentAt = LocalDateTime.now();
        }
    }

    public void markAsDelivered() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.DELIVERED;
        }
    }

    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.metadata = reason;
    }

    public boolean isRead() {
        return this.status == NotificationStatus.READ;
    }

    public boolean isPending() {
        return this.status == NotificationStatus.PENDING;
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isHighPriority() {
        return this.priority == NotificationPriority.HIGH || this.priority == NotificationPriority.URGENT;
    }

    public void schedule(LocalDateTime scheduledTime) {
        this.scheduledAt = scheduledTime;
        this.status = NotificationStatus.SCHEDULED;
    }

    public boolean isReadyToSend() {
        return (this.status == NotificationStatus.PENDING || this.status == NotificationStatus.SCHEDULED) &&
               (this.scheduledAt == null || LocalDateTime.now().isAfter(this.scheduledAt)) &&
               !isExpired();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }

    public String getEvidenceUuid() { return evidenceUuid; }
    public void setEvidenceUuid(String evidenceUuid) { this.evidenceUuid = evidenceUuid; }

    public String getValidatorUuid() { return validatorUuid; }
    public void setValidatorUuid(String validatorUuid) { this.validatorUuid = validatorUuid; }

    public String getValidationUuid() { return validationUuid; }
    public void setValidationUuid(String validationUuid) { this.validationUuid = validationUuid; }

    public String getChallengeUuid() { return challengeUuid; }
    public void setChallengeUuid(String challengeUuid) { this.challengeUuid = challengeUuid; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    public boolean isPushSent() { return pushSent; }
    public void setPushSent(boolean pushSent) { this.pushSent = pushSent; }

    public boolean isSmsSent() { return smsSent; }
    public void setSmsSent(boolean smsSent) { this.smsSent = smsSent; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
