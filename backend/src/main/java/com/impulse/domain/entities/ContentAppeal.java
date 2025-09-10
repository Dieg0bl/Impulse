package com.impulse.domain.entities;

import com.impulse.domain.enums.AppealStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Apelaciones a reportes de moderación
 * - Permite apelar decisiones de moderación
 * - Proceso de revisión secundaria
 * - Sistema de segunda oportunidad
 */
@Entity
@Table(name = "content_appeals",
       uniqueConstraints = @UniqueConstraint(columnNames = {"report_id"}),
       indexes = {
           @Index(name = "idx_appeals_report_id", columnList = "report_id"),
           @Index(name = "idx_appeals_user_id", columnList = "user_id"),
           @Index(name = "idx_appeals_status", columnList = "status"),
           @Index(name = "idx_appeals_reviewed_by", columnList = "reviewed_by")
       })
public class ContentAppeal {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", unique = true, nullable = false)
    @NotNull(message = "Report is required")
    private ContentReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Appeal reason is required")
    @Size(min = 20, max = 2000, message = "Reason must be between 20 and 2000 characters")
    private String reason;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Additional info cannot exceed 1000 characters")
    private String additionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppealStatus status = AppealStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewer;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "decision_reason", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Decision reason cannot exceed 1000 characters")
    private String decisionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // === CONSTRUCTORS ===

    public ContentAppeal() {}

    public ContentAppeal(ContentReport report, User user, String reason, String additionalInfo) {
        this.report = report;
        this.user = user;
        this.reason = reason;
        this.additionalInfo = additionalInfo;
        this.status = AppealStatus.PENDING;
    }

    // === BUSINESS METHODS ===

    /**
     * Inicia revisión de la apelación
     */
    public void startReview(User reviewer) {
        if (this.status != AppealStatus.PENDING) {
            throw new IllegalStateException("Appeal is not in pending status");
        }
        
        this.status = AppealStatus.UNDER_REVIEW;
        this.reviewer = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Acepta la apelación
     */
    public void accept(String decisionReason) {
        if (this.status != AppealStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Appeal is not under review");
        }
        
        this.status = AppealStatus.ACCEPTED;
        this.decisionReason = decisionReason;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Rechaza la apelación
     */
    public void reject(String decisionReason) {
        if (this.status != AppealStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Appeal is not under review");
        }
        
        this.status = AppealStatus.REJECTED;
        this.decisionReason = decisionReason;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Verifica si la apelación está pendiente
     */
    public boolean isPending() {
        return status == AppealStatus.PENDING;
    }

    /**
     * Verifica si la apelación está en revisión
     */
    public boolean isUnderReview() {
        return status == AppealStatus.UNDER_REVIEW;
    }

    /**
     * Verifica si la apelación fue aceptada
     */
    public boolean isAccepted() {
        return status == AppealStatus.ACCEPTED;
    }

    /**
     * Verifica si la apelación fue rechazada
     */
    public boolean isRejected() {
        return status == AppealStatus.REJECTED;
    }

    /**
     * Verifica si la apelación está resuelta
     */
    public boolean isResolved() {
        return status == AppealStatus.ACCEPTED || status == AppealStatus.REJECTED;
    }

    /**
     * Verifica si puede ser apelado (si el reporte original está resuelto)
     */
    public boolean canBeAppealed() {
        return report != null && report.isResolved() && !isResolved();
    }

    /**
     * Calcula días desde la creación de la apelación
     */
    public long getDaysSinceCreated() {
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt.toLocalDate(), 
                                                         LocalDateTime.now().toLocalDate());
    }

    // === GETTERS AND SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ContentReport getReport() { return report; }
    public void setReport(ContentReport report) { this.report = report; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public AppealStatus getStatus() { return status; }
    public void setStatus(AppealStatus status) { this.status = status; }

    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // === EQUALS, HASHCODE, TOSTRING ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentAppeal that = (ContentAppeal) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ContentAppeal{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", reviewedAt=" + reviewedAt +
                '}';
    }
}
