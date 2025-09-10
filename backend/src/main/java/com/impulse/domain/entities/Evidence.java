package com.impulse.domain.entities;

import com.impulse.domain.enums.EvidenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad Evidence - Evidencia subida por usuarios para challenges
 */
@Entity
@Table(name = "evidences", indexes = {
    @Index(name = "idx_evidences_challenge_id", columnList = "challengeId"),
    @Index(name = "idx_evidences_user_id", columnList = "userId"),
    @Index(name = "idx_evidences_status", columnList = "status"),
    @Index(name = "idx_evidences_type", columnList = "type"),
    @Index(name = "idx_evidences_created_at", columnList = "createdAt"),
    @Index(name = "idx_evidences_validation_status", columnList = "validationStatus")
})
@EntityListeners(AuditingEntityListener.class)
public class Evidence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull(message = "Challenge ID es requerido")
    private Long challengeId;
    
    @Column(nullable = false)
    @NotNull(message = "User ID es requerido")
    private Long userId;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo de evidencia es requerido")
    private EvidenceType type;
    
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Título es requerido")
    @Size(min = 3, max = 200, message = "Título debe tener entre 3 y 200 caracteres")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Descripción no puede exceder 2000 caracteres")
    private String description;
    
    @Column(length = 500)
    @Size(max = 500, message = "Contenido de texto no puede exceder 500 caracteres")
    private String textContent;
    
    @Column(length = 500)
    @Size(max = 500, message = "URL no puede exceder 500 caracteres")
    private String linkUrl;
    
    @Column(length = 255)
    @Size(max = 255, message = "Ruta de archivo no puede exceder 255 caracteres")
    private String filePath;
    
    @Column(length = 100)
    @Size(max = 100, message = "Nombre de archivo no puede exceder 100 caracteres")
    private String fileName;
    
    @Column(length = 50)
    @Size(max = 50, message = "Tipo MIME no puede exceder 50 caracteres")
    private String mimeType;
    
    @Column
    @Min(value = 0, message = "Tamaño de archivo no puede ser negativo")
    private Long fileSize;
    
    @Column(length = 255)
    @Size(max = 255, message = "URL de thumbnail no puede exceder 255 caracteres")
    private String thumbnailUrl;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EvidenceStatus status = EvidenceStatus.PENDING;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ValidationStatus validationStatus = ValidationStatus.PENDING;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal validationScore;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Validaciones completadas no puede ser negativo")
    private Integer validationsCompleted = 0;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Validaciones requeridas no puede ser negativo")
    private Integer validationsRequired = 3;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Validaciones positivas no puede ser negativo")
    private Integer validationsPositive = 0;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Validaciones negativas no puede ser negativo")
    private Integer validationsNegative = 0;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Feedback de validación no puede exceder 1000 caracteres")
    private String validationFeedback;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Puntos otorgados no puede ser negativo")
    private Integer pointsAwarded = 0;
    
    @Column(nullable = false)
    private Boolean flaggedForReview = false;
    
    @Column(length = 500)
    @Size(max = 500, message = "Motivo de flag no puede exceder 500 caracteres")
    private String flagReason;
    
    @Column
    private Long flaggedByUserId;
    
    @Column
    private LocalDateTime flaggedAt;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "evidence_metadata",
        joinColumns = @JoinColumn(name = "evidence_id"),
        indexes = @Index(name = "idx_evidence_metadata_evidence_id", columnList = "evidence_id")
    )
    @MapKeyColumn(name = "metadata_key", length = 50)
    @Column(name = "metadata_value", length = 255)
    private Map<String, String> metadata = new HashMap<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime validatedAt;
    
    // Constructors
    public Evidence() {}
    
    public Evidence(Long challengeId, Long userId, EvidenceType type, String title) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.type = type;
        this.title = title;
    }
    
    // Business Methods
    
    /**
     * Verifica si la evidencia está pendiente de validación
     */
    public boolean isPendingValidation() {
        return validationStatus == ValidationStatus.PENDING;
    }
    
    /**
     * Verifica si la evidencia está aprobada
     */
    public boolean isApproved() {
        return validationStatus == ValidationStatus.APPROVED;
    }
    
    /**
     * Verifica si la evidencia está rechazada
     */
    public boolean isRejected() {
        return validationStatus == ValidationStatus.REJECTED;
    }
    
    /**
     * Verifica si la evidencia está en revisión
     */
    public boolean isUnderReview() {
        return validationStatus == ValidationStatus.UNDER_REVIEW;
    }
    
    /**
     * Verifica si requiere archivo
     */
    public boolean requiresFile() {
        return type != EvidenceType.LINK && type != EvidenceType.TEXT;
    }
    
    /**
     * Verifica si es contenido de texto
     */
    public boolean isTextContent() {
        return type == EvidenceType.TEXT && textContent != null && !textContent.trim().isEmpty();
    }
    
    /**
     * Verifica si es un enlace
     */
    public boolean isLinkContent() {
        return type == EvidenceType.LINK && linkUrl != null && !linkUrl.trim().isEmpty();
    }
    
    /**
     * Verifica si tiene archivo adjunto
     */
    public boolean hasFile() {
        return filePath != null && !filePath.trim().isEmpty();
    }
    
    /**
     * Verifica si tiene suficientes validaciones
     */
    public boolean hasSufficientValidations() {
        return validationsCompleted >= validationsRequired;
    }
    
    /**
     * Calcula el porcentaje de aprobación
     */
    public double getApprovalPercentage() {
        if (validationsCompleted == 0) return 0.0;
        return (double) validationsPositive / validationsCompleted * 100.0;
    }
    
    /**
     * Verifica si debe ser aprobada automáticamente
     */
    public boolean shouldAutoApprove() {
        return hasSufficientValidations() && getApprovalPercentage() >= 70.0;
    }
    
    /**
     * Verifica si debe ser rechazada automáticamente
     */
    public boolean shouldAutoReject() {
        return hasSufficientValidations() && getApprovalPercentage() < 30.0;
    }
    
    /**
     * Agrega una validación positiva
     */
    public void addPositiveValidation() {
        this.validationsPositive++;
        this.validationsCompleted++;
        updateValidationStatus();
    }
    
    /**
     * Agrega una validación negativa
     */
    public void addNegativeValidation() {
        this.validationsNegative++;
        this.validationsCompleted++;
        updateValidationStatus();
    }
    
    /**
     * Actualiza el estado de validación basado en los votos
     */
    public void updateValidationStatus() {
        if (hasSufficientValidations()) {
            if (shouldAutoApprove()) {
                approve();
            } else if (shouldAutoReject()) {
                reject("Validación insuficiente por la comunidad");
            } else {
                setValidationStatus(ValidationStatus.UNDER_REVIEW);
            }
        }
    }
    
    /**
     * Aprueba la evidencia
     */
    public void approve() {
        this.validationStatus = ValidationStatus.APPROVED;
        this.validatedAt = LocalDateTime.now();
        this.status = EvidenceStatus.ACCEPTED;
        calculateValidationScore();
    }
    
    /**
     * Rechaza la evidencia
     */
    public void reject(String reason) {
        this.validationStatus = ValidationStatus.REJECTED;
        this.validatedAt = LocalDateTime.now();
        this.status = EvidenceStatus.REJECTED;
        this.validationFeedback = reason;
        this.validationScore = BigDecimal.ZERO;
    }
    
    /**
     * Marca para revisión manual
     */
    public void markForReview(String reason) {
        this.validationStatus = ValidationStatus.UNDER_REVIEW;
        this.validationFeedback = reason;
    }
    
    /**
     * Calcula el score de validación
     */
    public void calculateValidationScore() {
        if (validationsCompleted > 0) {
            double score = getApprovalPercentage() / 20.0; // Escala a 0-5
            this.validationScore = BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Marca como flagged
     */
    public void flag(String reason, Long flaggedByUserId) {
        this.flaggedForReview = true;
        this.flagReason = reason;
        this.flaggedByUserId = flaggedByUserId;
        this.flaggedAt = LocalDateTime.now();
    }
    
    /**
     * Remueve el flag
     */
    public void unflag() {
        this.flaggedForReview = false;
        this.flagReason = null;
        this.flaggedByUserId = null;
        this.flaggedAt = null;
    }
    
    /**
     * Otorga puntos
     */
    public void awardPoints(int points) {
        this.pointsAwarded = points;
    }
    
    /**
     * Establece información del archivo
     */
    public void setFileInfo(String fileName, String mimeType, Long fileSize, String filePath) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }
    
    /**
     * Obtiene el tamaño formateado
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) return "0 bytes";
        
        if (fileSize >= 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        } else if (fileSize >= 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return fileSize + " bytes";
        }
    }
    
    /**
     * Establece metadata
     */
    public void setMetadata(String key, String value) {
        if (value != null) {
            metadata.put(key, value);
        } else {
            metadata.remove(key);
        }
    }
    
    /**
     * Obtiene metadata
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Obtiene metadata con valor por defecto
     */
    public String getMetadata(String key, String defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public EvidenceStatus getStatus() { return status; }
    public void setStatus(EvidenceStatus status) { this.status = status; }
    
    public ValidationStatus getValidationStatus() { return validationStatus; }
    public void setValidationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; }
    
    public BigDecimal getValidationScore() { return validationScore; }
    public void setValidationScore(BigDecimal validationScore) { this.validationScore = validationScore; }
    
    public Integer getValidationsCompleted() { return validationsCompleted; }
    public void setValidationsCompleted(Integer validationsCompleted) { this.validationsCompleted = validationsCompleted; }
    
    public Integer getValidationsRequired() { return validationsRequired; }
    public void setValidationsRequired(Integer validationsRequired) { this.validationsRequired = validationsRequired; }
    
    public Integer getValidationsPositive() { return validationsPositive; }
    public void setValidationsPositive(Integer validationsPositive) { this.validationsPositive = validationsPositive; }
    
    public Integer getValidationsNegative() { return validationsNegative; }
    public void setValidationsNegative(Integer validationsNegative) { this.validationsNegative = validationsNegative; }
    
    public String getValidationFeedback() { return validationFeedback; }
    public void setValidationFeedback(String validationFeedback) { this.validationFeedback = validationFeedback; }
    
    public Integer getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
    
    public Boolean getFlaggedForReview() { return flaggedForReview; }
    public void setFlaggedForReview(Boolean flaggedForReview) { this.flaggedForReview = flaggedForReview; }
    
    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    
    public Long getFlaggedByUserId() { return flaggedByUserId; }
    public void setFlaggedByUserId(Long flaggedByUserId) { this.flaggedByUserId = flaggedByUserId; }
    
    public LocalDateTime getFlaggedAt() { return flaggedAt; }
    public void setFlaggedAt(LocalDateTime flaggedAt) { this.flaggedAt = flaggedAt; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    // Enums internos
    public enum EvidenceStatus {
        PENDING("Pendiente"),
        ACCEPTED("Aceptada"),
        REJECTED("Rechazada"),
        FLAGGED("Reportada");

        private final String displayName;

        EvidenceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
    
    public enum ValidationStatus {
        PENDING("Pendiente de validación"),
        UNDER_REVIEW("En revisión"),
        APPROVED("Aprobada"),
        REJECTED("Rechazada");

        private final String displayName;

        ValidationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
}
