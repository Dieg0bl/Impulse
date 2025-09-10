package com.impulse.domain.entities;

import com.impulse.domain.enums.CoachLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entidad Coach - Marketplace de coaches
 * - Gestión de niveles y scoring
 * - Integración con Stripe Connect
 * - Configuración de disponibilidad
 * - Especialidades y tarifas
 */
@Entity
@Table(name = "coaches", indexes = {
    @Index(name = "idx_coaches_user_id", columnList = "user_id"),
    @Index(name = "idx_coaches_level", columnList = "level"),
    @Index(name = "idx_coaches_score", columnList = "score"),
    @Index(name = "idx_coaches_is_active", columnList = "is_active")
})
public class Coach {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private CoachLevel level = CoachLevel.STARTER;

    @Column(name = "score", precision = 5, scale = 2, nullable = false)
    @DecimalMin(value = "0.00", message = "Score cannot be negative")
    @DecimalMax(value = "100.00", message = "Score cannot exceed 100")
    private BigDecimal score = BigDecimal.ZERO;

    // === STRIPE CONNECT ===
    
    @Column(name = "stripe_account_id", unique = true, length = 64)
    private String stripeAccountId;

    @Column(name = "stripe_onboarding_completed", nullable = false)
    private Boolean stripeOnboardingCompleted = false;

    @Column(name = "payout_enabled", nullable = false)
    private Boolean payoutEnabled = false;

    // === MARKETPLACE CONFIGURATION ===
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Hourly rate cannot be negative")
    @DecimalMax(value = "9999.99", message = "Hourly rate too high")
    private BigDecimal hourlyRate;

    @Column(name = "bio_coach", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
    private String bioCoach;

    @ElementCollection
    @CollectionTable(name = "coach_specialties", 
                     joinColumns = @JoinColumn(name = "coach_id"))
    @Column(name = "specialty")
    @Size(max = 10, message = "Maximum 10 specialties allowed")
    private List<String> specialties;

    @ElementCollection
    @CollectionTable(name = "coach_availability",
                     joinColumns = @JoinColumn(name = "coach_id"))
    @MapKeyColumn(name = "day_of_week")
    @Column(name = "time_slots", columnDefinition = "JSON")
    private Map<String, String> availability;

    // === AUDIT FIELDS ===
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === RELATIONSHIPS ===
    
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoachStatsWeekly> weeklyStats;

    // === CONSTRUCTORS ===
    
    public Coach() {}

    public Coach(User user) {
        this.user = user;
        this.level = CoachLevel.STARTER;
        this.score = BigDecimal.ZERO;
        this.isActive = false;
        this.stripeOnboardingCompleted = false;
        this.payoutEnabled = false;
    }

    // === BUSINESS METHODS ===
    
    /**
     * Activa el perfil de coach
     */
    public void activate() {
        if (!canBeActivated()) {
            throw new IllegalStateException("Coach cannot be activated without completing onboarding");
        }
        this.isActive = true;
    }

    /**
     * Desactiva el perfil de coach
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Verifica si el coach puede ser activado
     */
    public boolean canBeActivated() {
        return stripeOnboardingCompleted && 
               hourlyRate != null && 
               hourlyRate.compareTo(BigDecimal.ZERO) > 0 &&
               bioCoach != null && !bioCoach.trim().isEmpty();
    }

    /**
     * Actualiza el nivel basado en el score
     */
    public void updateLevelFromScore() {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            this.level = CoachLevel.CHAMPION;
        } else if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            this.level = CoachLevel.RISING;
        } else {
            this.level = CoachLevel.STARTER;
        }
    }

    /**
     * Completa onboarding de Stripe
     */
    public void completeStripeOnboarding(String accountId) {
        this.stripeAccountId = accountId;
        this.stripeOnboardingCompleted = true;
        this.payoutEnabled = true;
    }

    /**
     * Verifica si puede recibir pagos
     */
    public boolean canReceivePayments() {
        return stripeOnboardingCompleted && payoutEnabled && isActive;
    }

    /**
     * Calcula comisión de la plataforma según nivel
     */
    public BigDecimal calculatePlatformFee(BigDecimal amount) {
        BigDecimal feePercentage = switch (level) {
            case STARTER -> BigDecimal.valueOf(0.15);   // 15%
            case RISING -> BigDecimal.valueOf(0.12);    // 12%
            case CHAMPION -> BigDecimal.valueOf(0.10);  // 10%
        };
        
        return amount.multiply(feePercentage);
    }

    // === GETTERS AND SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CoachLevel getLevel() { return level; }
    public void setLevel(CoachLevel level) { this.level = level; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { 
        this.score = score;
        updateLevelFromScore();
    }

    public String getStripeAccountId() { return stripeAccountId; }
    public void setStripeAccountId(String stripeAccountId) { this.stripeAccountId = stripeAccountId; }

    public Boolean getStripeOnboardingCompleted() { return stripeOnboardingCompleted; }
    public void setStripeOnboardingCompleted(Boolean stripeOnboardingCompleted) { 
        this.stripeOnboardingCompleted = stripeOnboardingCompleted; 
    }

    public Boolean getPayoutEnabled() { return payoutEnabled; }
    public void setPayoutEnabled(Boolean payoutEnabled) { this.payoutEnabled = payoutEnabled; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getBioCoach() { return bioCoach; }
    public void setBioCoach(String bioCoach) { this.bioCoach = bioCoach; }

    public List<String> getSpecialties() { return specialties; }
    public void setSpecialties(List<String> specialties) { this.specialties = specialties; }

    public Map<String, String> getAvailability() { return availability; }
    public void setAvailability(Map<String, String> availability) { this.availability = availability; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<CoachStatsWeekly> getWeeklyStats() { return weeklyStats; }
    public void setWeeklyStats(List<CoachStatsWeekly> weeklyStats) { this.weeklyStats = weeklyStats; }

    // === EQUALS, HASHCODE, TOSTRING ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return id != null && id.equals(coach.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Coach{" +
                "id='" + id + '\'' +
                ", level=" + level +
                ", score=" + score +
                ", isActive=" + isActive +
                ", hourlyRate=" + hourlyRate +
                '}';
    }
}
