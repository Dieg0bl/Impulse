package com.impulse.domain.scoring;

import com.impulse.domain.entities.Evidence;
import com.impulse.domain.entities.EvidenceValidation;
import com.impulse.domain.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * Algoritmo ERSS - Evidence Review Scoring System
 * 
 * Evalúa la calidad y confiabilidad de las validaciones:
 * - Consistencia del validador (35%)
 * - Tiempo de respuesta (25%)
 * - Calidad del feedback (25%)
 * - Consenso con otros validadores (15%)
 * 
 * Score: 0-100 puntos
 */
@Component
public class ERSSScoreCalculator {
    
    private static final BigDecimal MAX_SCORE = BigDecimal.valueOf(100);
    
    /**
     * Calcula el ERSS score para una validación
     */
    public BigDecimal calculateERSSScore(EvidenceValidation validation, User validator,
                                        List<EvidenceValidation> validatorHistory,
                                        List<EvidenceValidation> evidenceValidations) {
        
        // Componentes del score
        BigDecimal consistencyScore = calculateConsistencyScore(validator, validatorHistory);
        BigDecimal responseScore = calculateResponseTimeScore(validation);
        BigDecimal feedbackScore = calculateFeedbackQualityScore(validation);
        BigDecimal consensusScore = calculateConsensusScore(validation, evidenceValidations);
        
        // Pesos: Consistency (35%) + Response (25%) + Feedback (25%) + Consensus (15%)
        BigDecimal totalScore = consistencyScore.multiply(BigDecimal.valueOf(0.35))
            .add(responseScore.multiply(BigDecimal.valueOf(0.25)))
            .add(feedbackScore.multiply(BigDecimal.valueOf(0.25)))
            .add(consensusScore.multiply(BigDecimal.valueOf(0.15)));
        
        return totalScore.min(MAX_SCORE).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula score de consistencia del validador (35% del ERSS)
     */
    private BigDecimal calculateConsistencyScore(User validator, List<EvidenceValidation> history) {
        if (history.isEmpty()) {
            return BigDecimal.valueOf(50); // Score neutral para validadores nuevos
        }
        
        // Filtrar validaciones de los últimos 30 días
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<EvidenceValidation> recentValidations = history.stream()
            .filter(v -> v.getValidatedAt().isAfter(thirtyDaysAgo))
            .toList();
        
        if (recentValidations.isEmpty()) {
            return BigDecimal.valueOf(30); // Penalizar inactividad
        }
        
        // Calcular tasa de aprobación
        long totalValidations = recentValidations.size();
        long approvals = recentValidations.stream()
            .mapToLong(v -> "APPROVED".equals(v.getDecision()) ? 1L : 0L)
            .sum();
        
        double approvalRate = (double) approvals / totalValidations;
        
        // Score base por balance (ni muy permisivo ni muy estricto)
        BigDecimal balanceScore;
        if (approvalRate >= 0.4 && approvalRate <= 0.7) {
            balanceScore = BigDecimal.valueOf(80); // Rango balanceado
        } else if (approvalRate >= 0.3 && approvalRate <= 0.8) {
            balanceScore = BigDecimal.valueOf(60); // Moderadamente balanceado
        } else {
            balanceScore = BigDecimal.valueOf(30); // Muy permisivo o muy estricto
        }
        
        // Bonus por volumen de validaciones
        BigDecimal volumeBonus = BigDecimal.valueOf(Math.min(totalValidations * 2, 20));
        
        return balanceScore.add(volumeBonus).min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula score de tiempo de respuesta (25% del ERSS)
     */
    private BigDecimal calculateResponseTimeScore(EvidenceValidation validation) {
        if (validation.getTimeTakenSeconds() == null) {
            return BigDecimal.valueOf(50); // Score neutral si no hay datos
        }
        
        int timeTaken = validation.getTimeTakenSeconds();
        
        // Score basado en tiempo (segundos)
        if (timeTaken <= 300) { // 5 minutos
            return BigDecimal.valueOf(100); // Respuesta muy rápida
        } else if (timeTaken <= 900) { // 15 minutos
            return BigDecimal.valueOf(80); // Respuesta rápida
        } else if (timeTaken <= 1800) { // 30 minutos
            return BigDecimal.valueOf(60); // Respuesta normal
        } else if (timeTaken <= 3600) { // 1 hora
            return BigDecimal.valueOf(40); // Respuesta lenta
        } else if (timeTaken <= 7200) { // 2 horas
            return BigDecimal.valueOf(20); // Respuesta muy lenta
        } else {
            return BigDecimal.valueOf(10); // Respuesta extremadamente lenta
        }
    }
    
    /**
     * Calcula score de calidad del feedback (25% del ERSS)
     */
    private BigDecimal calculateFeedbackQualityScore(EvidenceValidation validation) {
        String feedback = validation.getFeedback();
        
        // Sin feedback
        if (feedback == null || feedback.trim().isEmpty()) {
            // Para aprobaciones, feedback no es crítico
            if ("APPROVED".equals(validation.getDecision())) {
                return BigDecimal.valueOf(70);
            } else {
                return BigDecimal.valueOf(20); // Rechazo sin feedback es problemático
            }
        }
        
        // Análisis básico de calidad del feedback
        int length = feedback.length();
        BigDecimal baseScore;
        
        if (length < 20) {
            baseScore = BigDecimal.valueOf(30); // Muy corto
        } else if (length < 50) {
            baseScore = BigDecimal.valueOf(50); // Corto pero aceptable
        } else if (length < 150) {
            baseScore = BigDecimal.valueOf(80); // Buena longitud
        } else if (length < 300) {
            baseScore = BigDecimal.valueOf(90); // Detallado
        } else {
            baseScore = BigDecimal.valueOf(70); // Quizás demasiado largo
        }
        
        // Bonus por feedback constructivo (palabras clave)
        String lowerFeedback = feedback.toLowerCase();
        int constructiveWords = 0;
        
        String[] positiveWords = {"bien", "excelente", "correcto", "claro", "específico", "detallado"};
        String[] improvementWords = {"sugiero", "podrías", "considera", "mejora", "intenta"};
        String[] clarificationWords = {"porque", "ya que", "debido", "explicación", "razón"};
        
        for (String word : positiveWords) {
            if (lowerFeedback.contains(word)) constructiveWords++;
        }
        for (String word : improvementWords) {
            if (lowerFeedback.contains(word)) constructiveWords++;
        }
        for (String word : clarificationWords) {
            if (lowerFeedback.contains(word)) constructiveWords++;
        }
        
        BigDecimal qualityBonus = BigDecimal.valueOf(Math.min(constructiveWords * 5, 20));
        
        return baseScore.add(qualityBonus).min(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula score de consenso con otros validadores (15% del ERSS)
     */
    private BigDecimal calculateConsensusScore(EvidenceValidation validation, 
                                              List<EvidenceValidation> allValidations) {
        
        if (allValidations.size() <= 1) {
            return BigDecimal.valueOf(50); // Score neutral si es único validador
        }
        
        String thisDecision = validation.getDecision();
        long sameDecisions = allValidations.stream()
            .mapToLong(v -> thisDecision.equals(v.getDecision()) ? 1L : 0L)
            .sum();
        
        double consensusRate = (double) sameDecisions / allValidations.size();
        
        // Score basado en nivel de consenso
        if (consensusRate >= 0.8) {
            return BigDecimal.valueOf(100); // Alto consenso
        } else if (consensusRate >= 0.6) {
            return BigDecimal.valueOf(80); // Consenso moderado
        } else if (consensusRate >= 0.4) {
            return BigDecimal.valueOf(50); // Consenso bajo
        } else {
            return BigDecimal.valueOf(20); // Sin consenso
        }
    }
    
    /**
     * Calcula score de confianza del validador basado en historial
     */
    public BigDecimal calculateValidatorTrustScore(User validator, List<EvidenceValidation> history) {
        if (history.isEmpty()) {
            return BigDecimal.valueOf(50); // Score neutral para nuevos
        }
        
        // Promedio de ERSS scores de validaciones pasadas
        OptionalDouble averageERSS = history.stream()
            .filter(v -> v.getValidatedAt().isAfter(LocalDateTime.now().minusDays(90)))
            .mapToDouble(v -> calculateERSSScore(v, validator, history, List.of()).doubleValue())
            .average();
        
        if (averageERSS.isEmpty()) {
            return BigDecimal.valueOf(50);
        }
        
        BigDecimal baseScore = BigDecimal.valueOf(averageERSS.getAsDouble());
        
        // Bonus por experiencia
        int validationCount = history.size();
        BigDecimal experienceBonus = BigDecimal.valueOf(Math.min(validationCount * 0.5, 15));
        
        return baseScore.add(experienceBonus).min(BigDecimal.valueOf(100));
    }
    
    /**
     * Obtiene breakdown detallado del score ERSS
     */
    public Map<String, BigDecimal> getScoreBreakdown(EvidenceValidation validation, User validator,
                                                     List<EvidenceValidation> validatorHistory,
                                                     List<EvidenceValidation> evidenceValidations) {
        
        BigDecimal consistencyScore = calculateConsistencyScore(validator, validatorHistory);
        BigDecimal responseScore = calculateResponseTimeScore(validation);
        BigDecimal feedbackScore = calculateFeedbackQualityScore(validation);
        BigDecimal consensusScore = calculateConsensusScore(validation, evidenceValidations);
        
        BigDecimal totalScore = consistencyScore.multiply(BigDecimal.valueOf(0.35))
            .add(responseScore.multiply(BigDecimal.valueOf(0.25)))
            .add(feedbackScore.multiply(BigDecimal.valueOf(0.25)))
            .add(consensusScore.multiply(BigDecimal.valueOf(0.15)));
        
        return Map.of(
            "consistency_score", consistencyScore,
            "response_time_score", responseScore,
            "feedback_quality_score", feedbackScore,
            "consensus_score", consensusScore,
            "total_erss_score", totalScore.min(MAX_SCORE).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
