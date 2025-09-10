package com.impulse.domain.entities;

import com.impulse.domain.enums.ContentType;
import com.impulse.domain.enums.ReportReason;
import com.impulse.domain.enums.ReportStatus;
import com.impulse.domain.enums.ReportPriority;
import com.impulse.domain.enums.ActionTaken;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reportes de contenido para moderación
 * - Sistema de reportes de usuarios
 * - Workflow de moderación
 * - Acciones disciplinarias
 */
@Entity
@Table(name = "content_reports", indexes = {
    @Index(name = "idx_reports_reporter_id", columnList = "reporter_id"),
    @Index(name = "idx_reports_status", columnList = "status"),
    @Index(name = "idx_reports_priority", columnList = "priority"),
    @Index(name = "idx_reports_content", columnList = "content_type, content_id"),
    @Index(name = "idx_reports_created_at", columnList = "created_at"),
    @Index(name = "idx_reports_reviewed_by", columnList = "reviewed_by")
})
public class ContentReport {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @NotNull(message = "Reporter is required")
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    @NotNull(message = "Content type is required")
    private ContentType contentType;

    @Column(name = "content_id", columnDefinition = "CHAR(36)", nullable = false)
    @NotNull(message = "Content ID is required")
    private String contentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @NotNull(message = "Report reason is required")
    private ReportReason reason;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private ReportPriority priority = ReportPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewer;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "resolution", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Resolution cannot exceed 1000 characters")
    private String resolution;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken")
    private ActionTaken actionTaken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // === CONSTRUCTORS ===

    public ContentReport() {}

    public ContentReport(User reporter, ContentType contentType, String contentId, 
                        ReportReason reason, String description) {
        this.reporter = reporter;
        this.contentType = contentType;
        this.contentId = contentId;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
        this.priority = determinePriority(reason);
    }

    // === BUSINESS METHODS ===

    /**
     * Inicia revisión del reporte
     */
    public void startReview(User reviewer) {
        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException("Report is not in pending status");
        }
        
        this.status = ReportStatus.UNDER_REVIEW;
        this.reviewer = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Resuelve el reporte
     */
    public void resolve(String resolution, ActionTaken actionTaken) {
        if (this.status != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Report is not under review");
        }
        
        this.status = ReportStatus.RESOLVED;
        this.resolution = resolution;
        this.actionTaken = actionTaken;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Desestima el reporte
     */
    public void dismiss(String resolution) {
        if (this.status != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Report is not under review");
        }
        
        this.status = ReportStatus.DISMISSED;
        this.resolution = resolution;
        this.actionTaken = ActionTaken.NONE;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Determina prioridad automática basada en la razón
     */
    private ReportPriority determinePriority(ReportReason reason) {
        return switch (reason) {
            case HATE_SPEECH, HARASSMENT -> ReportPriority.URGENT;
            case SPAM, FALSE_INFO -> ReportPriority.HIGH;
            case COPYRIGHT, ADULT_CONTENT -> ReportPriority.MEDIUM;
            case OTHER -> ReportPriority.LOW;
        };
    }

    /**
     * Verifica si el reporte está pendiente
     */
    public boolean isPending() {
        return status == ReportStatus.PENDING;
    }

    /**
     * Verifica si el reporte está en revisión
     */
    public boolean isUnderReview() {
        return status == ReportStatus.UNDER_REVIEW;
    }

    /**
     * Verifica si el reporte está resuelto
     */
    public boolean isResolved() {
        return status == ReportStatus.RESOLVED || status == ReportStatus.DISMISSED;
    }

    /**
     * Verifica si requiere acción urgente
     */
    public boolean requiresUrgentAction() {
        return priority == ReportPriority.URGENT && !isResolved();
    }

    /**
     * Calcula días desde el reporte
     */
    public long getDaysSinceCreated() {
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt.toLocalDate(), 
                                                         LocalDateTime.now().toLocalDate());
    }

    // === GETTERS AND SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public ContentType getContentType() { return contentType; }
    public void setContentType(ContentType contentType) { this.contentType = contentType; }

    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }

    public ReportReason getReason() { return reason; }
    public void setReason(ReportReason reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public ReportPriority getPriority() { return priority; }
    public void setPriority(ReportPriority priority) { this.priority = priority; }

    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public ActionTaken getActionTaken() { return actionTaken; }
    public void setActionTaken(ActionTaken actionTaken) { this.actionTaken = actionTaken; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // === EQUALS, HASHCODE, TOSTRING ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentReport that = (ContentReport) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ContentReport{" +
                "id='" + id + '\'' +
                ", contentType=" + contentType +
                ", reason=" + reason +
                ", status=" + status +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}
