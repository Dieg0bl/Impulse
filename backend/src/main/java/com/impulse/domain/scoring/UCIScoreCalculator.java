package com.impulse.domain.scoring;

import com.impulse.domain.entities.Challenge;
import com.impulse.domain.entities.Evidence;
import com.impulse.domain.entities.User;
import com.impulse.domain.entities.EvidenceValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Algoritmo UCI - User Consistency Index
 * 
 * Mide la consistencia del usuario en:
 * - Completar retos iniciados (40%)
 * - Actividad regular (30%) 
 * - Calidad de evidencias (30%)
 * 
 * Score: 0-100 puntos
 */
@Component
public class UCIScoreCalculator {
    
    private static final BigDecimal MAX_SCORE = BigDecimal.valueOf(100);
    private static final int EVALUATION_DAYS = 90; // Evaluar últimos 90 días
    
    /**
     * Calcula el UCI score para un usuario
     */
    public BigDecimal calculateUCIScore(User user, List<Challenge> challenges, 
                                       List<Evidence> evidences) {
        
        // Filtrar datos de los últimos 90 días
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(EVALUATION_DAYS);
        
        List<Challenge> recentChallenges = challenges.stream()
            .filter(c -> c.getCreatedAt().isAfter(cutoffDate))
            .collect(Collectors.toList());
            
        List<Evidence> recentEvidences = evidences.stream()
            .filter(e -> e.getSubmittedAt().isAfter(cutoffDate))
            .collect(Collectors.toList());
        
        if (recentChallenges.isEmpty() && recentEvidences.isEmpty()) {
            return BigDecimal.ZERO; // Sin actividad reciente
        }
        
        // Componentes del score
        BigDecimal completionScore = calculateCompletionScore(recentChallenges);
        BigDecimal activityScore = calculateActivityScore(user, cutoffDate);
        BigDecimal qualityScore = calculateQualityScore(recentEvidences);
        
        // Pesos: Completion (40%) + Activity (30%) + Quality (30%)
        BigDecimal totalScore = completionScore.multiply(BigDecimal.valueOf(0.40))
            .add(activityScore.multiply(BigDecimal.valueOf(0.30)))
            .add(qualityScore.multiply(BigDecimal.valueOf(0.30)));
        
        return totalScore.min(MAX_SCORE).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula score de completación de retos (40% del UCI)
     */
    private BigDecimal calculateCompletionScore(List<Challenge> challenges) {
        if (challenges.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        long totalChallenges = challenges.size();
        long completedChallenges = challenges.stream()
            .mapToLong(challenge -> challenge.isCompleted() ? 1L : 0L)
            .sum();
        
        // Score base por tasa de completación
        double completionRate = (double) completedChallenges / totalChallenges;
        BigDecimal baseScore = BigDecimal.valueOf(completionRate * 70); // Máx 70 puntos
        
        // Bonus por retos completados en tiempo
        long onTimeChallenges = challenges.stream()
            .filter(Challenge::isCompleted)
            .mapToLong(challenge -> challenge.isCompletedOnTime() ? 1L : 0L)
            .sum();
        
        if (completedChallenges > 0) {
            double onTimeRate = (double) onTimeChallenges / completedChallenges;
            BigDecimal timeBonus = BigDecimal.valueOf(onTimeRate * 30); // Máx 30 puntos bonus
            baseScore = baseScore.add(timeBonus);
        }
        
        return baseScore.min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula score de actividad regular (30% del UCI)
     */
    private BigDecimal calculateActivityScore(User user, LocalDateTime cutoffDate) {
        // Calcular días activos en el período
        long totalDays = ChronoUnit.DAYS.between(cutoffDate, LocalDateTime.now());
        if (totalDays == 0) return BigDecimal.ZERO;
        
        // Simular actividad diaria - en implementación real vendría de logs
        long activeDays = Math.min(totalDays, 60); // Simulamos actividad
        
        // Score base por frecuencia de actividad
        double activityRate = (double) activeDays / totalDays;
        BigDecimal baseScore = BigDecimal.valueOf(activityRate * 60); // Máx 60 puntos
        
        // Bonus por racha de días consecutivos
        int currentStreak = calculateActivityStreak(user);
        BigDecimal streakBonus = BigDecimal.valueOf(Math.min(currentStreak * 2, 40)); // Máx 40 puntos
        
        return baseScore.add(streakBonus).min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula score de calidad de evidencias (30% del UCI)
     */
    private BigDecimal calculateQualityScore(List<Evidence> evidences) {
        if (evidences.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Tasa de aprobación de evidencias
        long totalEvidences = evidences.size();
        long approvedEvidences = evidences.stream()
            .mapToLong(evidence -> evidence.isApproved() ? 1L : 0L)
            .sum();
        
        double approvalRate = (double) approvedEvidences / totalEvidences;
        BigDecimal baseScore = BigDecimal.valueOf(approvalRate * 70); // Máx 70 puntos
        
        // Bonus por validaciones rápidas (primer intento)
        long firstTryApprovals = evidences.stream()
            .filter(Evidence::isApproved)
            .mapToLong(evidence -> evidence.isFirstTryApproval() ? 1L : 0L)
            .sum();
        
        if (approvedEvidences > 0) {
            double firstTryRate = (double) firstTryApprovals / approvedEvidences;
            BigDecimal qualityBonus = BigDecimal.valueOf(firstTryRate * 30); // Máx 30 puntos
            baseScore = baseScore.add(qualityBonus);
        }
        
        return baseScore.min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula racha actual de días activos
     */
    private int calculateActivityStreak(User user) {
        // En implementación real, consultaríamos logs de actividad
        // Por ahora retornamos un valor simulado basado en la última actividad
        
        if (user.getLastLoginAt() == null) {
            return 0;
        }
        
        long daysSinceLastLogin = ChronoUnit.DAYS.between(
            user.getLastLoginAt().toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        );
        
        // Simulamos una racha basada en actividad reciente
        if (daysSinceLastLogin <= 1) {
            return 7; // Racha de 7 días si login reciente
        } else if (daysSinceLastLogin <= 3) {
            return 3; // Racha corta
        } else {
            return 0; // Sin racha
        }
    }
    
    /**
     * Obtiene breakdown detallado del score
     */
    public Map<String, BigDecimal> getScoreBreakdown(User user, List<Challenge> challenges, 
                                                     List<Evidence> evidences) {
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(EVALUATION_DAYS);
        
        List<Challenge> recentChallenges = challenges.stream()
            .filter(c -> c.getCreatedAt().isAfter(cutoffDate))
            .collect(Collectors.toList());
            
        List<Evidence> recentEvidences = evidences.stream()
            .filter(e -> e.getSubmittedAt().isAfter(cutoffDate))
            .collect(Collectors.toList());
        
        BigDecimal completionScore = calculateCompletionScore(recentChallenges);
        BigDecimal activityScore = calculateActivityScore(user, cutoffDate);
        BigDecimal qualityScore = calculateQualityScore(recentEvidences);
        
        BigDecimal totalScore = completionScore.multiply(BigDecimal.valueOf(0.40))
            .add(activityScore.multiply(BigDecimal.valueOf(0.30)))
            .add(qualityScore.multiply(BigDecimal.valueOf(0.30)));
        
        return Map.of(
            "completion_score", completionScore,
            "activity_score", activityScore,
            "quality_score", qualityScore,
            "total_uci_score", totalScore.min(MAX_SCORE).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
