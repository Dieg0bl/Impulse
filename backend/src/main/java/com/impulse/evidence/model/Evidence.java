package com.impulse.evidence.model;

import com.impulse.user.model.User;
import com.impulse.challenge.model.ChallengeParticipation;
import com.impulse.validation.model.ValidationMethod;
import com.impulse.evidence.model.EvidenceType;
import com.impulse.evidence.model.EvidenceStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Evidence Domain Entity
 * 
 * Represents evidence submitted by users to prove challenge progress
 * Supports multiple content types and validation workflows
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "evidences",
       indexes = {
           @Index(name = "idx_evidences_participation_id", columnList = "participation_id"),
           @Index(name = "idx_evidences_day_number", columnList = "day_number"),
           @Index(name = "idx_evidences_status", columnList = "status"),
           @Index(name = "idx_evidences_submitted_at", columnList = "submitted_at")
       })
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private ChallengeParticipation participation;

    @Min(1)
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EvidenceType type;

    @Size(max = 5000)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // URL for files, text content, or description

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Min(0)
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Size(max = 2000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata; // JSON object with additional metadata

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvidenceStatus status = EvidenceStatus.PENDING;

    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(name = "validation_score", precision = 3, scale = 2)
    private BigDecimal validationScore;

    @Size(max = 1000)
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    // Relationships
    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvidenceValidation> validations = new ArrayList<>();

    // Constructors
    public Evidence() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Evidence(ChallengeParticipation participation, Integer dayNumber, EvidenceType type) {
        this();
        this.participation = participation;
        this.dayNumber = dayNumber;
        this.type = type;
    }

    // Business methods
    public void approve(BigDecimal score) {
        status = EvidenceStatus.APPROVED;
        validationScore = score;
        validatedAt = LocalDateTime.now();
        
        // Update participation progress
        participation.updateProgress(dayNumber);
    }

    public void reject(String reason) {
        status = EvidenceStatus.REJECTED;
        rejectionReason = reason;
        validatedAt = LocalDateTime.now();
    }

    public void flag(String reason) {
        status = EvidenceStatus.FLAGGED;
        rejectionReason = reason;
    }

    public boolean isValidated() {
        return validatedAt != null;
    }

    public boolean isApproved() {
        return status == EvidenceStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == EvidenceStatus.REJECTED;
    }

    public boolean isPending() {
        return status == EvidenceStatus.PENDING;
    }

    public boolean isFlagged() {
        return status == EvidenceStatus.FLAGGED;
    }

    public boolean requiresValidation() {
        ValidationMethod method = participation.getChallenge().getValidationMethod();
        return method.requiresHumanValidation() && isPending();
    }

    public boolean canBeValidatedBy(User validator) {
        ValidationMethod method = participation.getChallenge().getValidationMethod();
        
        // Cannot validate own evidence
        if (validator.equals(participation.getUser())) {
            return false;
        }
        
        // Check role requirements
        return validator.getRole().hasLevel(method.getMinimumValidatorRole());
    }

    public int getValidationCount() {
        return validations.size();
    }

    public int getRequiredValidationCount() {
        ValidationMethod method = participation.getChallenge().getValidationMethod();
        if (method == ValidationMethod.PEER) {
            return 3; // Configurable
        } else if (method == ValidationMethod.MODERATOR) {
            return 1;
        }
        return 0;
    }

    public BigDecimal getAverageValidationScore() {
        if (validations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = validations.stream()
                .map(EvidenceValidation::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return sum.divide(new BigDecimal(validations.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    public boolean hasFileAttachment() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    public String getFileExtension() {
        if (filePath == null) return null;
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot + 1) : null;
    }

    public boolean isImageType() {
        return type == EvidenceType.IMAGE || 
               (mimeType != null && mimeType.startsWith("image/"));
    }

    public boolean isVideoType() {
        return type == EvidenceType.VIDEO || 
               (mimeType != null && mimeType.startsWith("video/"));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public ChallengeParticipation getParticipation() { return participation; }
    public void setParticipation(ChallengeParticipation participation) { this.participation = participation; }

    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }

    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public EvidenceStatus getStatus() { return status; }
    public void setStatus(EvidenceStatus status) { this.status = status; }

    public BigDecimal getValidationScore() { return validationScore; }
    public void setValidationScore(BigDecimal validationScore) { this.validationScore = validationScore; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public List<EvidenceValidation> getValidations() { return validations; }
    public void setValidations(List<EvidenceValidation> validations) { this.validations = validations; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(uuid, evidence.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", dayNumber=" + dayNumber +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
