package com.impulse.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.enums.CoachLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Estadísticas semanales de coaches
 * - Métricas de rendimiento semanal
 * - Cálculo de scores y niveles
 * - Historial de evolución
 */
@Entity
@Table(name = "coach_stats_weekly", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"coach_id", "week_start"}),
       indexes = {
           @Index(name = "idx_coach_stats_coach_id", columnList = "coach_id"),
           @Index(name = "idx_coach_stats_week_start", columnList = "week_start")
       })
public class CoachStatsWeekly {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    @JsonIgnore
    private Coach coach;

    @Column(name = "week_start", nullable = false)
    @NotNull(message = "Week start is required")
    private LocalDate weekStart;

    @Column(name = "metrics_json", columnDefinition = "JSON", nullable = false)
    @Lob
    private String metricsJson;

    @Column(name = "score", precision = 5, scale = 2, nullable = false)
    @DecimalMin(value = "0.00", message = "Score cannot be negative")
    @DecimalMax(value = "100.00", message = "Score cannot exceed 100")
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private CoachLevel level;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Transient field para las métricas deserializadas
    @Transient
    private Map<String, Object> metrics;

    // ObjectMapper para serialización JSON
    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // === CONSTRUCTORS ===

    public CoachStatsWeekly() {}

    public CoachStatsWeekly(Coach coach, LocalDate weekStart) {
        this.coach = coach;
        this.weekStart = weekStart;
        this.metrics = new HashMap<>();
        this.score = BigDecimal.ZERO;
        this.level = CoachLevel.STARTER;
    }

    // === BUSINESS METHODS ===

    /**
     * Actualiza las métricas de la semana
     */
    public void updateMetrics(Map<String, Object> newMetrics) {
        this.metrics = new HashMap<>(newMetrics);
        this.metricsJson = serializeMetrics();
        calculateScore();
    }

    /**
     * Calcula el score basado en las métricas
     */
    private void calculateScore() {
        if (metrics == null || metrics.isEmpty()) {
            this.score = BigDecimal.ZERO;
            this.level = CoachLevel.STARTER;
            return;
        }

        double calculatedScore = 0.0;

        // Métricas de rendimiento (40% del score)
        calculatedScore += getMetricValue("sessions_completed", 0.0) * 2.0; // Máx 20 puntos
        calculatedScore += getMetricValue("average_rating", 0.0) * 4.0;     // Máx 20 puntos

        // Métricas de compromiso (30% del score)
        calculatedScore += getMetricValue("response_time_hours", 24.0) <= 2.0 ? 15.0 : 0.0; // Respuesta rápida
        calculatedScore += getMetricValue("availability_percentage", 0.0) * 0.15; // Máx 15 puntos

        // Métricas de calidad (30% del score)
        double completionRate = getMetricValue("completion_rate", 0.0);
        calculatedScore += completionRate * 20.0; // Máx 20 puntos
        
        double repeatClientRate = getMetricValue("repeat_client_rate", 0.0);
        calculatedScore += repeatClientRate * 10.0; // Máx 10 puntos

        // Normalizar a 100
        this.score = BigDecimal.valueOf(Math.min(100.0, Math.max(0.0, calculatedScore)));
        this.level = CoachLevel.fromScore(this.score.intValue());
    }

    /**
     * Obtiene valor de métrica con default
     */
    private double getMetricValue(String key, double defaultValue) {
        if (metrics == null || !metrics.containsKey(key)) {
            return defaultValue;
        }
        
        Object value = metrics.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        return defaultValue;
    }

    /**
     * Serializa métricas a JSON
     */
    private String serializeMetrics() {
        try {
            return objectMapper.writeValueAsString(metrics != null ? metrics : new HashMap<>());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing metrics", e);
        }
    }

    /**
     * Deserializa métricas desde JSON
     */
    @PostLoad
    private void deserializeMetrics() {
        if (metricsJson != null && !metricsJson.trim().isEmpty()) {
            try {
                this.metrics = objectMapper.readValue(metricsJson, Map.class);
            } catch (JsonProcessingException e) {
                this.metrics = new HashMap<>();
            }
        } else {
            this.metrics = new HashMap<>();
        }
    }

    /**
     * Agrega métrica individual
     */
    public void addMetric(String key, Object value) {
        if (this.metrics == null) {
            this.metrics = new HashMap<>();
        }
        this.metrics.put(key, value);
        this.metricsJson = serializeMetrics();
    }

    /**
     * Obtiene métrica individual
     */
    public Object getMetric(String key) {
        return metrics != null ? metrics.get(key) : null;
    }

    /**
     * Verifica si hay mejora respecto a la semana anterior
     */
    public boolean hasImprovedFrom(CoachStatsWeekly previousWeek) {
        if (previousWeek == null) return true;
        return this.score.compareTo(previousWeek.getScore()) > 0;
    }

    // === GETTERS AND SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach coach) { this.coach = coach; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public String getMetricsJson() { return metricsJson; }
    public void setMetricsJson(String metricsJson) { 
        this.metricsJson = metricsJson;
        deserializeMetrics();
    }

    public Map<String, Object> getMetrics() { 
        if (metrics == null) {
            deserializeMetrics();
        }
        return metrics; 
    }

    public void setMetrics(Map<String, Object> metrics) { 
        this.metrics = metrics;
        this.metricsJson = serializeMetrics();
    }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public CoachLevel getLevel() { return level; }
    public void setLevel(CoachLevel level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // === EQUALS, HASHCODE, TOSTRING ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachStatsWeekly that = (CoachStatsWeekly) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CoachStatsWeekly{" +
                "id='" + id + '\'' +
                ", weekStart=" + weekStart +
                ", score=" + score +
                ", level=" + level +
                '}';
    }
}
