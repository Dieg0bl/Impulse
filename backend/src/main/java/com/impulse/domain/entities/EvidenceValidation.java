package com.impulse.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Entidad EvidenceValidation - Validación de evidencia por parte de validadores
 */
@Entity
@Table(name = "evidence_validations", indexes = {
    @Index(name = "idx_evidence_validations_evidence_id", columnList = "evidenceId"),
    @Index(name = "idx_evidence_validations_validator_id", columnList = "validatorId"),
    @Index(name = "idx_evidence_validations_status", columnList = "status"),
    @Index(name = "idx_evidence_validations_created_at", columnList = "createdAt"),
    @Index(name = "idx_evidence_validations_decision", columnList = "decision")
},
uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_evidence_validator", 
        columnNames = {"evidenceId", "validatorId"}
    )
})
@EntityListeners(AuditingEntityListener.class)
public class EvidenceValidation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull(message = "Evidence ID es requerido")
    private Long evidenceId;
    
    @Column(nullable = false)
    @NotNull(message = "Validator ID es requerido")
    private Long validatorId;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ValidationStatus status = ValidationStatus.PENDING;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ValidationDecision decision;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00", message = "Score no puede ser negativo")
    @DecimalMax(value = "5.00", message = "Score no puede exceder 5.00")
    private BigDecimal qualityScore;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00", message = "Score no puede ser negativo")
    @DecimalMax(value = "5.00", message = "Score no puede exceder 5.00")
    private BigDecimal relevanceScore;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00", message = "Score no puede ser negativo")
    @DecimalMax(value = "5.00", message = "Score no puede exceder 5.00")
    private BigDecimal completenessScore;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00", message = "Score no puede ser negativo")
    @DecimalMax(value = "5.00", message = "Score no puede exceder 5.00")
    private BigDecimal overallScore;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Comentarios no pueden exceder 2000 caracteres")
    private String comments;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Feedback no puede exceder 1000 caracteres")
    private String feedback;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "Mejoras sugeridas no pueden exceder 500 caracteres")
    private String suggestedImprovements;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "validation_criteria_scores",
        joinColumns = @JoinColumn(name = "validation_id"),
        indexes = @Index(name = "idx_validation_criteria_validation_id", columnList = "validation_id")
    )
    @MapKeyColumn(name = "criteria_name", length = 50)
    @Column(name = "criteria_score", precision = 3, scale = 2)
    private Map<String, BigDecimal> criteriaScores = new HashMap<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "validation_flags",
        joinColumns = @JoinColumn(name = "validation_id"),
        indexes = @Index(name = "idx_validation_flags_validation_id", columnList = "validation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "flag_type")
    private Set<ValidationFlag> flags = new HashSet<>();
    
    @Column(nullable = false)
    @Min(value = 0, message = "Tiempo dedicado no puede ser negativo")
    private Integer timeSpentMinutes = 0;
    
    @Column(nullable = false)
    private Boolean isConsistentWithPrevious = true;
    
    @Column(precision = 5, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal confidenceLevel;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ValidationPriority priority = ValidationPriority.NORMAL;
    
    @Column
    private LocalDateTime assignedAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime lastActivityAt;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "validation_metadata",
        joinColumns = @JoinColumn(name = "validation_id"),
        indexes = @Index(name = "idx_validation_metadata_validation_id", columnList = "validation_id")
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
    
    // Constructors
    public EvidenceValidation() {}
    
    public EvidenceValidation(Long evidenceId, Long validatorId) {
        this.evidenceId = evidenceId;
        this.validatorId = validatorId;
        this.assignedAt = LocalDateTime.now();
    }
    
    // Business Methods
    
    /**
     * Verifica si la validación está pendiente
     */
    public boolean isPending() {
        return status == ValidationStatus.PENDING;
    }
    
    /**
     * Verifica si la validación está en progreso
     */
    public boolean isInProgress() {
        return status == ValidationStatus.IN_PROGRESS;
    }
    
    /**
     * Verifica si la validación está completada
     */
    public boolean isCompleted() {
        return status == ValidationStatus.COMPLETED;
    }
    
    /**
     * Verifica si la validación está expirada
     */
    public boolean isExpired() {
        return status == ValidationStatus.EXPIRED;
    }
    
    /**
     * Verifica si la decisión es positiva
     */
    public boolean isApproved() {
        return decision == ValidationDecision.APPROVE;
    }
    
    /**
     * Verifica si la decisión es negativa
     */
    public boolean isRejected() {
        return decision == ValidationDecision.REJECT;
    }
    
    /**
     * Verifica si requiere revisión
     */
    public boolean needsReview() {
        return decision == ValidationDecision.NEEDS_REVIEW;
    }
    
    /**
     * Inicia la validación
     */
    public void start() {
        if (status != ValidationStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden iniciar validaciones pendientes");
        }
        
        this.status = ValidationStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }
    
    /**
     * Completa la validación con aprobación
     */
    public void approve(BigDecimal qualityScore, BigDecimal relevanceScore, 
                       BigDecimal completenessScore, String feedback) {
        validateScores(qualityScore, relevanceScore, completenessScore);
        
        this.decision = ValidationDecision.APPROVE;
        this.qualityScore = qualityScore;
        this.relevanceScore = relevanceScore;
        this.completenessScore = completenessScore;
        this.feedback = feedback;
        
        calculateOverallScore();
        complete();
    }
    
    /**
     * Completa la validación con rechazo
     */
    public void reject(String feedback, String suggestedImprovements) {
        this.decision = ValidationDecision.REJECT;
        this.feedback = feedback;
        this.suggestedImprovements = suggestedImprovements;
        this.overallScore = BigDecimal.ZERO;
        
        complete();
    }
    
    /**
     * Marca para revisión adicional
     */
    public void markForReview(String comments) {
        this.decision = ValidationDecision.NEEDS_REVIEW;
        this.comments = comments;
        
        complete();
    }
    
    /**
     * Completa la validación
     */
    private void complete() {
        this.status = ValidationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }
    
    /**
     * Marca como expirada
     */
    public void expire() {
        if (status == ValidationStatus.PENDING || status == ValidationStatus.IN_PROGRESS) {
            this.status = ValidationStatus.EXPIRED;
            this.lastActivityAt = LocalDateTime.now();
        }
    }
    
    /**
     * Calcula el score general
     */
    public void calculateOverallScore() {
        if (qualityScore != null && relevanceScore != null && completenessScore != null) {
            // Pesos: Calidad 40%, Relevancia 35%, Completitud 25%
            BigDecimal weighted = qualityScore.multiply(BigDecimal.valueOf(0.40))
                    .add(relevanceScore.multiply(BigDecimal.valueOf(0.35)))
                    .add(completenessScore.multiply(BigDecimal.valueOf(0.25)));
            
            this.overallScore = weighted.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Valida que los scores estén en el rango correcto
     */
    private void validateScores(BigDecimal quality, BigDecimal relevance, BigDecimal completeness) {
        if (quality == null || relevance == null || completeness == null) {
            throw new IllegalArgumentException("Todos los scores son requeridos");
        }
        
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.valueOf(5.00);
        
        if (quality.compareTo(min) < 0 || quality.compareTo(max) > 0 ||
            relevance.compareTo(min) < 0 || relevance.compareTo(max) > 0 ||
            completeness.compareTo(min) < 0 || completeness.compareTo(max) > 0) {
            throw new IllegalArgumentException("Los scores deben estar entre 0.00 y 5.00");
        }
    }
    
    /**
     * Agrega un flag de validación
     */
    public void addFlag(ValidationFlag flag) {
        this.flags.add(flag);
    }
    
    /**
     * Remueve un flag de validación
     */
    public void removeFlag(ValidationFlag flag) {
        this.flags.remove(flag);
    }
    
    /**
     * Verifica si tiene un flag específico
     */
    public boolean hasFlag(ValidationFlag flag) {
        return flags.contains(flag);
    }
    
    /**
     * Establece score para un criterio específico
     */
    public void setCriteriaScore(String criteria, BigDecimal score) {
        if (score != null) {
            criteriaScores.put(criteria, score);
        } else {
            criteriaScores.remove(criteria);
        }
    }
    
    /**
     * Obtiene score de un criterio específico
     */
    public BigDecimal getCriteriaScore(String criteria) {
        return criteriaScores.get(criteria);
    }
    
    /**
     * Calcula el tiempo dedicado en la validación
     */
    public void updateTimeSpent() {
        if (startedAt != null && completedAt != null) {
            long minutes = java.time.Duration.between(startedAt, completedAt).toMinutes();
            this.timeSpentMinutes = (int) Math.max(0, minutes);
        }
    }
    
    /**
     * Actualiza la actividad
     */
    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }
    
    /**
     * Establece el nivel de confianza
     */
    public void setConfidence(BigDecimal confidence) {
        if (confidence != null) {
            BigDecimal min = BigDecimal.ZERO;
            BigDecimal max = BigDecimal.valueOf(100.00);
            
            if (confidence.compareTo(min) >= 0 && confidence.compareTo(max) <= 0) {
                this.confidenceLevel = confidence;
            }
        }
    }
    
    /**
     * Verifica si es alta prioridad
     */
    public boolean isHighPriority() {
        return priority == ValidationPriority.HIGH;
    }
    
    /**
     * Verifica si es urgente
     */
    public boolean isUrgent() {
        return priority == ValidationPriority.URGENT;
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
    
    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
    
    public Long getValidatorId() { return validatorId; }
    public void setValidatorId(Long validatorId) { this.validatorId = validatorId; }
    
    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }
    
    public ValidationDecision getDecision() { return decision; }
    public void setDecision(ValidationDecision decision) { this.decision = decision; }
    
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
    
    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }
    
    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }
    
    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public String getSuggestedImprovements() { return suggestedImprovements; }
    public void setSuggestedImprovements(String suggestedImprovements) { this.suggestedImprovements = suggestedImprovements; }
    
    public Map<String, BigDecimal> getCriteriaScores() { return criteriaScores; }
    public void setCriteriaScores(Map<String, BigDecimal> criteriaScores) { this.criteriaScores = criteriaScores; }
    
    public Set<ValidationFlag> getFlags() { return flags; }
    public void setFlags(Set<ValidationFlag> flags) { this.flags = flags; }
    
    public Integer getTimeSpentMinutes() { return timeSpentMinutes; }
    public void setTimeSpentMinutes(Integer timeSpentMinutes) { this.timeSpentMinutes = timeSpentMinutes; }
    
    public Boolean getIsConsistentWithPrevious() { return isConsistentWithPrevious; }
    public void setIsConsistentWithPrevious(Boolean isConsistentWithPrevious) { this.isConsistentWithPrevious = isConsistentWithPrevious; }
    
    public BigDecimal getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    
    public ValidationPriority getPriority() { return priority; }
    public void setPriority(ValidationPriority priority) { this.priority = priority; }
    
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums internos
    public enum ValidationStatus {
        PENDING("Pendiente"),
        IN_PROGRESS("En progreso"),
        COMPLETED("Completada"),
        EXPIRED("Expirada");

        private final String displayName;

        ValidationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
    
    public enum ValidationDecision {
        APPROVE("Aprobar"),
        REJECT("Rechazar"),
        NEEDS_REVIEW("Requiere revisión");

        private final String displayName;

        ValidationDecision(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
    
    public enum ValidationFlag {
        POTENTIAL_SPAM("Posible spam"),
        INAPPROPRIATE_CONTENT("Contenido inapropiado"),
        LOW_QUALITY("Baja calidad"),
        DUPLICATE_CONTENT("Contenido duplicado"),
        SUSPICIOUS_ACTIVITY("Actividad sospechosa"),
        NEEDS_EXPERT_REVIEW("Requiere revisión experta");

        private final String displayName;

        ValidationFlag(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
    
    public enum ValidationPriority {
        LOW("Baja", 1),
        NORMAL("Normal", 2),
        HIGH("Alta", 3),
        URGENT("Urgente", 4);

        private final String displayName;
        private final int level;

        ValidationPriority(String displayName, int level) {
            this.displayName = displayName;
            this.level = level;
        }

        public String getDisplayName() { return displayName; }
        public int getLevel() { return level; }
    }
}
