package com.impulse.domain.entities;

import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.EvidenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad Challenge - Reto o desafío del sistema
 */
@Entity
@Table(name = "challenges", indexes = {
    @Index(name = "idx_challenges_creator_id", columnList = "creatorId"),
    @Index(name = "idx_challenges_status", columnList = "status"),
    @Index(name = "idx_challenges_category", columnList = "category"),
    @Index(name = "idx_challenges_start_date", columnList = "startDate"),
    @Index(name = "idx_challenges_end_date", columnList = "endDate"),
    @Index(name = "idx_challenges_created_at", columnList = "createdAt"),
    @Index(name = "idx_challenges_featured", columnList = "featured")
})
@EntityListeners(AuditingEntityListener.class)
public class Challenge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Título es requerido")
    @Size(min = 5, max = 200, message = "Título debe tener entre 5 y 200 caracteres")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Descripción es requerida")
    @Size(min = 20, max = 5000, message = "Descripción debe tener entre 20 y 5000 caracteres")
    private String description;
    
    @Column(length = 100)
    @NotBlank(message = "Categoría es requerida")
    @Size(max = 100, message = "Categoría no puede exceder 100 caracteres")
    private String category;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "challenge_tags",
        joinColumns = @JoinColumn(name = "challenge_id"),
        indexes = @Index(name = "idx_challenge_tags_challenge_id", columnList = "challenge_id")
    )
    @Column(name = "tag", length = 50)
    @Size(max = 10, message = "Máximo 10 tags permitidos")
    private Set<String> tags = new HashSet<>();
    
    @Column(nullable = false)
    @NotNull(message = "Creator ID es requerido")
    private Long creatorId;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status = ChallengeStatus.DRAFT;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ChallengeDifficulty difficulty;
    
    @Column(nullable = false)
    @NotNull(message = "Fecha de inicio es requerida")
    @Future(message = "Fecha de inicio debe ser futura")
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    @NotNull(message = "Fecha de fin es requerida")
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Duración en días debe ser al menos 1")
    @Max(value = 365, message = "Duración en días no puede exceder 365")
    private Integer durationInDays;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Puntos de recompensa no pueden ser negativos")
    @Max(value = 10000, message = "Puntos de recompensa no pueden exceder 10000")
    private Integer rewardPoints = 0;
    
    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Recompensa monetaria no puede ser negativa")
    @DecimalMax(value = "10000.00", message = "Recompensa monetaria no puede exceder 10000")
    private BigDecimal monetaryReward;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "challenge_evidence_types",
        joinColumns = @JoinColumn(name = "challenge_id"),
        indexes = @Index(name = "idx_challenge_evidence_types_challenge_id", columnList = "challenge_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type")
    private Set<EvidenceType> allowedEvidenceTypes = new HashSet<>();
    
    @Column(nullable = false)
    @Min(value = 1, message = "Mínimo de evidencias debe ser al menos 1")
    @Max(value = 20, message = "Mínimo de evidencias no puede exceder 20")
    private Integer minEvidences = 1;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Máximo de evidencias debe ser al menos 1")
    @Max(value = 50, message = "Máximo de evidencias no puede exceder 50")
    private Integer maxEvidences = 10;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Mínimo de participantes debe ser al menos 1")
    @Max(value = 10000, message = "Mínimo de participantes no puede exceder 10000")
    private Integer minParticipants = 1;
    
    @Column
    @Min(value = 1, message = "Máximo de participantes debe ser al menos 1")
    @Max(value = 100000, message = "Máximo de participantes no puede exceder 100000")
    private Integer maxParticipants;
    
    @Column(nullable = false)
    private Boolean requiresValidation = true;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Validadores requeridos debe ser al menos 1")
    @Max(value = 10, message = "Validadores requeridos no puede exceder 10")
    private Integer requiredValidators = 3;
    
    @Column(nullable = false)
    private Boolean allowsCoaching = false;
    
    @Column(nullable = false)
    private Boolean featured = false;
    
    @Column(nullable = false)
    private Boolean publiclyVisible = true;
    
    @Column(length = 255)
    @Size(max = 255, message = "URL de imagen no puede exceder 255 caracteres")
    private String imageUrl;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "challenge_rules",
        joinColumns = @JoinColumn(name = "challenge_id"),
        indexes = @Index(name = "idx_challenge_rules_challenge_id", columnList = "challenge_id")
    )
    @Column(name = "rule", columnDefinition = "TEXT")
    @Size(max = 20, message = "Máximo 20 reglas permitidas")
    private Set<String> rules = new HashSet<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "challenge_metadata",
        joinColumns = @JoinColumn(name = "challenge_id"),
        indexes = @Index(name = "idx_challenge_metadata_challenge_id", columnList = "challenge_id")
    )
    @MapKeyColumn(name = "metadata_key", length = 50)
    @Column(name = "metadata_value", length = 255)
    private Map<String, String> metadata = new HashMap<>();
    
    // Campos estadísticos (calculados)
    @Column(nullable = false)
    private Integer participantCount = 0;
    
    @Column(nullable = false)
    private Integer evidenceCount = 0;
    
    @Column(nullable = false)
    private Integer completedCount = 0;
    
    @Column(precision = 5, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal completionRate = BigDecimal.ZERO;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Integer viewCount = 0;
    
    @Column(nullable = false)
    private Integer shareCount = 0;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime publishedAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    // Constructors
    public Challenge() {}
    
    public Challenge(String title, String description, String category, Long creatorId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.creatorId = creatorId;
        this.allowedEvidenceTypes.add(EvidenceType.IMAGE);
        this.allowedEvidenceTypes.add(EvidenceType.TEXT);
    }
    
    // Business Methods
    
    /**
     * Valida las fechas del challenge
     */
    @AssertTrue(message = "Fecha de fin debe ser posterior a fecha de inicio")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) return true; // Se validará por @NotNull
        return endDate.isAfter(startDate);
    }
    
    /**
     * Valida los límites de participantes
     */
    @AssertTrue(message = "Máximo de participantes debe ser mayor o igual al mínimo")
    public boolean isValidParticipantRange() {
        if (maxParticipants == null) return true;
        return maxParticipants >= minParticipants;
    }
    
    /**
     * Valida los límites de evidencias
     */
    @AssertTrue(message = "Máximo de evidencias debe ser mayor o igual al mínimo")
    public boolean isValidEvidenceRange() {
        return maxEvidences >= minEvidences;
    }
    
    /**
     * Verifica si el challenge está activo
     */
    public boolean isActive() {
        return status == ChallengeStatus.ACTIVE;
    }
    
    /**
     * Verifica si el challenge ha iniciado
     */
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDate);
    }
    
    /**
     * Verifica si el challenge ha expirado
     */
    public boolean hasExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }
    
    /**
     * Verifica si puede recibir evidencias
     */
    public boolean canReceiveEvidences() {
        return status == ChallengeStatus.ACTIVE && 
               hasStarted() && 
               !hasExpired();
    }
    
    /**
     * Verifica si puede ser editado
     */
    public boolean canBeEdited() {
        return status == ChallengeStatus.DRAFT;
    }
    
    /**
     * Verifica si está completo (tiene participantes suficientes)
     */
    public boolean hasMinimumParticipants() {
        return participantCount >= minParticipants;
    }
    
    /**
     * Verifica si ha alcanzado el máximo de participantes
     */
    public boolean hasReachedMaxParticipants() {
        return maxParticipants != null && participantCount >= maxParticipants;
    }
    
    /**
     * Verifica si permite un tipo de evidencia específico
     */
    public boolean allowsEvidenceType(EvidenceType type) {
        return allowedEvidenceTypes.contains(type);
    }
    
    /**
     * Inicia el challenge
     */
    public void start() {
        if (status != ChallengeStatus.DRAFT) {
            throw new IllegalStateException("Solo se pueden iniciar challenges en estado DRAFT");
        }
        if (!hasMinimumParticipants()) {
            throw new IllegalStateException("No se puede iniciar sin el mínimo de participantes");
        }
        
        this.status = ChallengeStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
        this.publishedAt = LocalDateTime.now();
    }
    
    /**
     * Completa el challenge
     */
    public void complete() {
        if (status != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("Solo se pueden completar challenges activos");
        }
        
        this.status = ChallengeStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        calculateCompletionRate();
    }
    
    /**
     * Cancela el challenge
     */
    public void cancel() {
        if (status == ChallengeStatus.COMPLETED) {
            throw new IllegalStateException("No se puede cancelar un challenge completado");
        }
        
        this.status = ChallengeStatus.CANCELLED;
    }
    
    /**
     * Marca como expirado
     */
    public void expire() {
        if (status == ChallengeStatus.ACTIVE) {
            this.status = ChallengeStatus.EXPIRED;
            calculateCompletionRate();
        }
    }
    
    /**
     * Agrega un participante
     */
    public void addParticipant() {
        if (hasReachedMaxParticipants()) {
            throw new IllegalStateException("Se ha alcanzado el máximo de participantes");
        }
        this.participantCount++;
    }
    
    /**
     * Remueve un participante
     */
    public void removeParticipant() {
        if (participantCount > 0) {
            this.participantCount--;
        }
    }
    
    /**
     * Agrega una evidencia
     */
    public void addEvidence() {
        this.evidenceCount++;
    }
    
    /**
     * Incrementa el contador de completados
     */
    public void incrementCompleted() {
        this.completedCount++;
        calculateCompletionRate();
    }
    
    /**
     * Calcula la tasa de completado
     */
    private void calculateCompletionRate() {
        if (participantCount > 0) {
            double rate = (double) completedCount / participantCount * 100;
            this.completionRate = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Incrementa el contador de vistas
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    /**
     * Incrementa el contador de compartidos
     */
    public void incrementShareCount() {
        this.shareCount++;
    }
    
    /**
     * Actualiza el rating promedio
     */
    public void updateAverageRating(BigDecimal newRating) {
        this.averageRating = newRating;
    }
    
    /**
     * Agrega una regla
     */
    public void addRule(String rule) {
        if (rule != null && !rule.trim().isEmpty()) {
            this.rules.add(rule.trim());
        }
    }
    
    /**
     * Remueve una regla
     */
    public void removeRule(String rule) {
        this.rules.remove(rule);
    }
    
    /**
     * Agrega un tag
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && tags.size() < 10) {
            this.tags.add(tag.trim().toLowerCase());
        }
    }
    
    /**
     * Remueve un tag
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
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
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    
    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }
    
    public ChallengeDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(ChallengeDifficulty difficulty) { this.difficulty = difficulty; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public Integer getDurationInDays() { return durationInDays; }
    public void setDurationInDays(Integer durationInDays) { this.durationInDays = durationInDays; }
    
    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    
    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public void setMonetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; }
    
    public Set<EvidenceType> getAllowedEvidenceTypes() { return allowedEvidenceTypes; }
    public void setAllowedEvidenceTypes(Set<EvidenceType> allowedEvidenceTypes) { this.allowedEvidenceTypes = allowedEvidenceTypes; }
    
    public Integer getMinEvidences() { return minEvidences; }
    public void setMinEvidences(Integer minEvidences) { this.minEvidences = minEvidences; }
    
    public Integer getMaxEvidences() { return maxEvidences; }
    public void setMaxEvidences(Integer maxEvidences) { this.maxEvidences = maxEvidences; }
    
    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }
    
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public Boolean getRequiresValidation() { return requiresValidation; }
    public void setRequiresValidation(Boolean requiresValidation) { this.requiresValidation = requiresValidation; }
    
    public Integer getRequiredValidators() { return requiredValidators; }
    public void setRequiredValidators(Integer requiredValidators) { this.requiredValidators = requiredValidators; }
    
    public Boolean getAllowsCoaching() { return allowsCoaching; }
    public void setAllowsCoaching(Boolean allowsCoaching) { this.allowsCoaching = allowsCoaching; }
    
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    
    public Boolean getPubliclyVisible() { return publiclyVisible; }
    public void setPubliclyVisible(Boolean publiclyVisible) { this.publiclyVisible = publiclyVisible; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Set<String> getRules() { return rules; }
    public void setRules(Set<String> rules) { this.rules = rules; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public Integer getParticipantCount() { return participantCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }
    
    public Integer getEvidenceCount() { return evidenceCount; }
    public void setEvidenceCount(Integer evidenceCount) { this.evidenceCount = evidenceCount; }
    
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    
    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getShareCount() { return shareCount; }
    public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    // Enum interno
    public enum ChallengeDifficulty {
        BEGINNER("Principiante", 1, 100),
        INTERMEDIATE("Intermedio", 2, 250),
        ADVANCED("Avanzado", 3, 500),
        EXPERT("Experto", 4, 1000),
        MASTER("Maestro", 5, 2000);

        private final String displayName;
        private final int level;
        private final int basePoints;

        ChallengeDifficulty(String displayName, int level, int basePoints) {
            this.displayName = displayName;
            this.level = level;
            this.basePoints = basePoints;
        }

        public String getDisplayName() { return displayName; }
        public int getLevel() { return level; }
        public int getBasePoints() { return basePoints; }
    }
}
