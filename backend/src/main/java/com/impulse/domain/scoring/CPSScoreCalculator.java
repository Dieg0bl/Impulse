package com.impulse.domain.scoring;

import com.impulse.domain.entities.Challenge;
import com.impulse.domain.entities.Evidence;
import com.impulse.domain.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Algoritmo CPS - Challenge Priority Score
 * 
 * Determina la prioridad de un reto basado en:
 * - Urgencia temporal (40%)
 * - Dificultad declarada (25%)
 * - Engagement del creador (20%) 
 * - Valor para la comunidad (15%)
 * 
 * Score: 0-1 (decimal para ordenamiento)
 */
@Component
public class CPSScoreCalculator {
    
    private static final BigDecimal MAX_SCORE = BigDecimal.ONE;
    
    /**
     * Calcula el CPS score para un reto
     */
    public BigDecimal calculateCPSScore(Challenge challenge, User creator, 
                                       List<Evidence> creatorEvidences) {
        
        // Componentes del score
        BigDecimal urgencyScore = calculateUrgencyScore(challenge);
        BigDecimal difficultyScore = calculateDifficultyScore(challenge);
        BigDecimal engagementScore = calculateEngagementScore(creator, creatorEvidences);
        BigDecimal communityScore = calculateCommunityScore(challenge);
        
        // Pesos: Urgency (40%) + Difficulty (25%) + Engagement (20%) + Community (15%)
        BigDecimal totalScore = urgencyScore.multiply(BigDecimal.valueOf(0.40))
            .add(difficultyScore.multiply(BigDecimal.valueOf(0.25)))
            .add(engagementScore.multiply(BigDecimal.valueOf(0.20)))
            .add(communityScore.multiply(BigDecimal.valueOf(0.15)));
        
        return totalScore.min(MAX_SCORE).setScale(3, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula score de urgencia temporal (40% del CPS)
     */
    private BigDecimal calculateUrgencyScore(Challenge challenge) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();
        
        // Si el reto ya empezó
        if (now.isAfter(startDate) || now.isEqual(startDate)) {
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long remainingDays = ChronoUnit.DAYS.between(now, endDate);
            
            if (remainingDays <= 0) {
                return BigDecimal.ZERO; // Reto expirado
            }
            
            // Score inversamente proporcional a días restantes
            double urgencyRatio = 1.0 - ((double) remainingDays / totalDays);
            return BigDecimal.valueOf(Math.max(0.1, urgencyRatio)); // Mínimo 0.1
        }
        
        // Si el reto aún no empezó
        long daysUntilStart = ChronoUnit.DAYS.between(now, startDate);
        if (daysUntilStart <= 7) {
            return BigDecimal.valueOf(0.8); // Empieza pronto, alta prioridad
        } else if (daysUntilStart <= 30) {
            return BigDecimal.valueOf(0.5); // Empieza en el mes, prioridad media
        } else {
            return BigDecimal.valueOf(0.2); // Empieza lejano, baja prioridad
        }
    }
    
    /**
     * Calcula score de dificultad (25% del CPS)
     */
    private BigDecimal calculateDifficultyScore(Challenge challenge) {
        int difficultyLevel = challenge.getDifficultyLevel();
        
        // Normalizar nivel de dificultad (1-5) a score (0-1)
        // Mayor dificultad = mayor prioridad para balancear
        return switch (difficultyLevel) {
            case 1 -> BigDecimal.valueOf(0.2); // Muy fácil
            case 2 -> BigDecimal.valueOf(0.4); // Fácil  
            case 3 -> BigDecimal.valueOf(0.6); // Medio
            case 4 -> BigDecimal.valueOf(0.8); // Difícil
            case 5 -> BigDecimal.valueOf(1.0); // Muy difícil
            default -> BigDecimal.valueOf(0.5); // Default medio
        };
    }
    
    /**
     * Calcula score de engagement del creador (20% del CPS)
     */
    private BigDecimal calculateEngagementScore(User creator, List<Evidence> creatorEvidences) {
        if (creator == null) {
            return BigDecimal.valueOf(0.3); // Score neutral para usuarios desconocidos
        }
        
        // Días desde registro
        long daysSinceRegistration = ChronoUnit.DAYS.between(
            creator.getCreatedAt().toLocalDate(), 
            LocalDate.now()
        );
        
        // Score base por antigüedad (usuarios nuevos tienen prioridad)
        BigDecimal seniority;
        if (daysSinceRegistration <= 30) {
            seniority = BigDecimal.valueOf(1.0); // Usuario nuevo, máxima prioridad
        } else if (daysSinceRegistration <= 90) {
            seniority = BigDecimal.valueOf(0.8); // Usuario reciente
        } else if (daysSinceRegistration <= 365) {
            seniority = BigDecimal.valueOf(0.6); // Usuario establecido
        } else {
            seniority = BigDecimal.valueOf(0.4); // Usuario veterano
        }
        
        // Bonus por actividad reciente (evidencias en últimos 30 días)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentEvidences = creatorEvidences.stream()
            .filter(e -> e.getSubmittedAt().isAfter(thirtyDaysAgo))
            .count();
        
        BigDecimal activityBonus = BigDecimal.valueOf(Math.min(recentEvidences * 0.1, 0.3));
        
        return seniority.add(activityBonus).min(BigDecimal.ONE);
    }
    
    /**
     * Calcula score de valor comunitario (15% del CPS)
     */
    private BigDecimal calculateCommunityScore(Challenge challenge) {
        BigDecimal baseScore = BigDecimal.valueOf(0.5); // Score base neutral
        
        // Bonus por visibilidad pública
        if ("PUBLIC".equals(challenge.getVisibility())) {
            baseScore = baseScore.add(BigDecimal.valueOf(0.3));
        }
        
        // Bonus por categoría popular
        String category = challenge.getCategory();
        BigDecimal categoryBonus = switch (category.toLowerCase()) {
            case "fitness", "health", "learning" -> BigDecimal.valueOf(0.2); // Categorías populares
            case "productivity", "creativity" -> BigDecimal.valueOf(0.15);   // Categorías útiles
            case "social", "fun" -> BigDecimal.valueOf(0.1);                 // Categorías sociales
            default -> BigDecimal.ZERO;                                      // Otras categorías
        };
        
        baseScore = baseScore.add(categoryBonus);
        
        // Penalización por retos muy largos (más de 90 días)
        long duration = ChronoUnit.DAYS.between(challenge.getStartDate(), challenge.getEndDate());
        if (duration > 90) {
            baseScore = baseScore.subtract(BigDecimal.valueOf(0.2));
        }
        
        return baseScore.max(BigDecimal.valueOf(0.1)).min(BigDecimal.ONE);
    }
    
    /**
     * Determina prioridad categórica basada en CPS
     */
    public String getPriorityCategory(BigDecimal cpsScore) {
        if (cpsScore.compareTo(BigDecimal.valueOf(0.8)) >= 0) {
            return "URGENT";
        } else if (cpsScore.compareTo(BigDecimal.valueOf(0.6)) >= 0) {
            return "HIGH";
        } else if (cpsScore.compareTo(BigDecimal.valueOf(0.4)) >= 0) {
            return "MEDIUM";
        } else if (cpsScore.compareTo(BigDecimal.valueOf(0.2)) >= 0) {
            return "LOW";
        } else {
            return "MINIMAL";
        }
    }
    
    /**
     * Obtiene breakdown detallado del score CPS
     */
    public Map<String, BigDecimal> getScoreBreakdown(Challenge challenge, User creator,
                                                     List<Evidence> creatorEvidences) {
        
        BigDecimal urgencyScore = calculateUrgencyScore(challenge);
        BigDecimal difficultyScore = calculateDifficultyScore(challenge);
        BigDecimal engagementScore = calculateEngagementScore(creator, creatorEvidences);
        BigDecimal communityScore = calculateCommunityScore(challenge);
        
        BigDecimal totalScore = urgencyScore.multiply(BigDecimal.valueOf(0.40))
            .add(difficultyScore.multiply(BigDecimal.valueOf(0.25)))
            .add(engagementScore.multiply(BigDecimal.valueOf(0.20)))
            .add(communityScore.multiply(BigDecimal.valueOf(0.15)));
        
        return Map.of(
            "urgency_score", urgencyScore,
            "difficulty_score", difficultyScore,
            "engagement_score", engagementScore,
            "community_score", communityScore,
            "total_cps_score", totalScore.min(MAX_SCORE).setScale(3, RoundingMode.HALF_UP),
            "priority_category", new BigDecimal(getPriorityCategory(totalScore))
        );
    }
}
