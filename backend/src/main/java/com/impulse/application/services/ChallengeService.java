package com.impulse.application.services;

import com.impulse.domain.entities.Challenge;
import com.impulse.domain.entities.Challenge.ChallengeDifficulty;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.EvidenceType;
import com.impulse.infrastructure.persistence.repositories.ChallengeRepository;
import com.impulse.infrastructure.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de challenges
 */
@Service
@Transactional
public class ChallengeService {
    
    private static final String CHALLENGE_NOT_FOUND = "Challenge no encontrado";
    
    @Autowired
    private ChallengeRepository challengeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // CRUD Operations
    
    /**
     * Crea un nuevo challenge
     */
    public Challenge createChallenge(String title, String description, String category, 
                                   Long creatorId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCreator(creatorId);
        validateDates(startDate, endDate);
        
        Challenge challenge = new Challenge(title, description, category, creatorId);
        challenge.setStartDate(startDate);
        challenge.setEndDate(endDate);
        
        // Calcular duración en días
        long days = java.time.Duration.between(startDate, endDate).toDays();
        challenge.setDurationInDays((int) days);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Crea un challenge completo con todas las opciones
     */
    public Challenge createCompleteChallenge(String title, String description, String category,
                                           Long creatorId, LocalDateTime startDate, LocalDateTime endDate,
                                           ChallengeDifficulty difficulty, Integer rewardPoints,
                                           BigDecimal monetaryReward, Set<EvidenceType> allowedTypes,
                                           Integer minEvidences, Integer maxEvidences,
                                           Integer minParticipants, Integer maxParticipants,
                                           Boolean requiresValidation, Integer requiredValidators,
                                           Boolean allowsCoaching, Set<String> tags) {
        
        Challenge challenge = createChallenge(title, description, category, creatorId, startDate, endDate);
        
        // Configuración adicional
        challenge.setDifficulty(difficulty);
        challenge.setRewardPoints(rewardPoints);
        challenge.setMonetaryReward(monetaryReward);
        challenge.setAllowedEvidenceTypes(allowedTypes);
        challenge.setMinEvidences(minEvidences);
        challenge.setMaxEvidences(maxEvidences);
        challenge.setMinParticipants(minParticipants);
        challenge.setMaxParticipants(maxParticipants);
        challenge.setRequiresValidation(requiresValidation);
        challenge.setRequiredValidators(requiredValidators);
        challenge.setAllowsCoaching(allowsCoaching);
        challenge.setTags(tags);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Obtiene un challenge por ID
     */
    @Transactional(readOnly = true)
    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }
    
    /**
     * Obtiene un challenge por ID y incrementa las vistas
     */
    public Optional<Challenge> getChallengeByIdAndIncrementViews(Long id) {
        Optional<Challenge> challengeOpt = challengeRepository.findById(id);
        if (challengeOpt.isPresent()) {
            Challenge challenge = challengeOpt.get();
            challenge.incrementViewCount();
            challengeRepository.save(challenge);
        }
        return challengeOpt;
    }
    
    /**
     * Actualiza un challenge
     */
    public Challenge updateChallenge(Long challengeId, String title, String description,
                                   String category, LocalDateTime startDate, LocalDateTime endDate) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.canBeEdited()) {
            throw new RuntimeException("Solo se pueden editar challenges en estado DRAFT");
        }
        
        validateDates(startDate, endDate);
        
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setCategory(category);
        challenge.setStartDate(startDate);
        challenge.setEndDate(endDate);
        
        // Recalcular duración
        long days = java.time.Duration.between(startDate, endDate).toDays();
        challenge.setDurationInDays((int) days);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Elimina un challenge (solo si está en DRAFT)
     */
    public void deleteChallenge(Long challengeId, Long requestingUserId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.getCreatorId().equals(requestingUserId)) {
            throw new RuntimeException("Solo el creador puede eliminar el challenge");
        }
        
        if (!challenge.canBeEdited()) {
            throw new RuntimeException("Solo se pueden eliminar challenges en estado DRAFT");
        }
        
        challengeRepository.delete(challenge);
    }
    
    // State Management
    
    /**
     * Inicia un challenge
     */
    public Challenge startChallenge(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.start();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Completa un challenge
     */
    public Challenge completeChallenge(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.complete();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Cancela un challenge
     */
    public Challenge cancelChallenge(Long challengeId, Long requestingUserId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.getCreatorId().equals(requestingUserId)) {
            throw new RuntimeException("Solo el creador puede cancelar el challenge");
        }
        
        challenge.cancel();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Expira challenges automáticamente
     */
    @Transactional
    public List<Challenge> expireOldChallenges() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> expiredChallenges = challengeRepository.findExpiredChallenges(now);
        
        for (Challenge challenge : expiredChallenges) {
            challenge.expire();
        }
        
        return challengeRepository.saveAll(expiredChallenges);
    }
    
    // Participation Management
    
    /**
     * Agrega un participante al challenge
     */
    public Challenge addParticipant(Long challengeId, Long userId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        validateUser(userId);
        
        if (!challenge.canReceiveEvidences()) {
            throw new RuntimeException("El challenge no está activo para recibir participantes");
        }
        
        challenge.addParticipant();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Remueve un participante del challenge
     */
    public Challenge removeParticipant(Long challengeId, Long userId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.removeParticipant();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Incrementa el contador de evidencias
     */
    public Challenge addEvidence(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.addEvidence();
        return challengeRepository.save(challenge);
    }
    
    /**
     * Incrementa el contador de completados
     */
    public Challenge incrementCompleted(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.incrementCompleted();
        return challengeRepository.save(challenge);
    }
    
    // Configuration Management
    
    /**
     * Actualiza la configuración de evidencias
     */
    public Challenge updateEvidenceConfiguration(Long challengeId, Set<EvidenceType> allowedTypes,
                                                Integer minEvidences, Integer maxEvidences) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.canBeEdited()) {
            throw new RuntimeException("Solo se puede modificar la configuración en estado DRAFT");
        }
        
        challenge.setAllowedEvidenceTypes(allowedTypes);
        challenge.setMinEvidences(minEvidences);
        challenge.setMaxEvidences(maxEvidences);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Actualiza la configuración de participantes
     */
    public Challenge updateParticipantConfiguration(Long challengeId, Integer minParticipants,
                                                   Integer maxParticipants) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.canBeEdited()) {
            throw new RuntimeException("Solo se puede modificar la configuración en estado DRAFT");
        }
        
        challenge.setMinParticipants(minParticipants);
        challenge.setMaxParticipants(maxParticipants);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Actualiza la configuración de validación
     */
    public Challenge updateValidationConfiguration(Long challengeId, Boolean requiresValidation,
                                                  Integer requiredValidators) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        if (!challenge.canBeEdited()) {
            throw new RuntimeException("Solo se puede modificar la configuración en estado DRAFT");
        }
        
        challenge.setRequiresValidation(requiresValidation);
        challenge.setRequiredValidators(requiredValidators);
        
        return challengeRepository.save(challenge);
    }
    
    /**
     * Marca/desmarca como destacado
     */
    public Challenge toggleFeatured(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.setFeatured(!challenge.getFeatured());
        return challengeRepository.save(challenge);
    }
    
    /**
     * Incrementa el contador de compartidos
     */
    public Challenge incrementShareCount(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        challenge.incrementShareCount();
        return challengeRepository.save(challenge);
    }
    
    // Search and Listing
    
    /**
     * Busca challenges por texto
     */
    @Transactional(readOnly = true)
    public Page<Challenge> searchChallenges(String search, Pageable pageable) {
        return challengeRepository.searchChallenges(search, pageable);
    }
    
    /**
     * Obtiene challenges por categoría
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getChallengesByCategory(String category, Pageable pageable) {
        return challengeRepository.findByCategory(category, pageable);
    }
    
    /**
     * Obtiene challenges por status
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getChallengesByStatus(ChallengeStatus status, Pageable pageable) {
        return challengeRepository.findByStatus(status, pageable);
    }
    
    /**
     * Obtiene challenges del creador
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getChallengesByCreator(Long creatorId, Pageable pageable) {
        return challengeRepository.findByCreatorId(creatorId, pageable);
    }
    
    /**
     * Obtiene challenges destacados
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getFeaturedChallenges(Pageable pageable) {
        return challengeRepository.findByFeaturedAndPubliclyVisibleAndStatus(
            true, true, ChallengeStatus.ACTIVE, pageable);
    }
    
    /**
     * Obtiene challenges populares
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getPopularChallenges(Pageable pageable) {
        return challengeRepository.findPopularChallenges(pageable);
    }
    
    /**
     * Obtiene challenges trending
     */
    @Transactional(readOnly = true)
    public List<Challenge> getTrendingChallenges(LocalDateTime since) {
        return challengeRepository.findTrendingChallenges(since);
    }
    
    /**
     * Obtiene challenges mejor valorados
     */
    @Transactional(readOnly = true)
    public Page<Challenge> getTopRatedChallenges(Pageable pageable) {
        return challengeRepository.findTopRatedChallenges(pageable);
    }
    
    /**
     * Obtiene challenges actualmente activos
     */
    @Transactional(readOnly = true)
    public List<Challenge> getCurrentlyActiveChallenges() {
        return challengeRepository.findCurrentlyActive(LocalDateTime.now());
    }
    
    /**
     * Obtiene challenges próximos
     */
    @Transactional(readOnly = true)
    public List<Challenge> getUpcomingChallenges() {
        return challengeRepository.findUpcomingChallenges(LocalDateTime.now());
    }
    
    /**
     * Obtiene todas las categorías
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return challengeRepository.findAllCategories();
    }
    
    // Recommendations
    
    /**
     * Obtiene challenges similares
     */
    @Transactional(readOnly = true)
    public List<Challenge> getSimilarChallenges(Long challengeId, Pageable pageable) {
        Challenge challenge = getChallengeById(challengeId)
            .orElseThrow(() -> new RuntimeException(CHALLENGE_NOT_FOUND));
        
        return challengeRepository.findSimilarChallenges(
            challenge.getCategory(), challengeId, pageable);
    }
    
    // Statistics
    
    /**
     * Cuenta challenges por status
     */
    @Transactional(readOnly = true)
    public long countChallengesByStatus(ChallengeStatus status) {
        return challengeRepository.countByStatus(status);
    }
    
    /**
     * Cuenta challenges por creador
     */
    @Transactional(readOnly = true)
    public long countChallengesByCreator(Long creatorId) {
        return challengeRepository.countByCreatorId(creatorId);
    }
    
    /**
     * Obtiene estadísticas de challenges
     */
    @Transactional(readOnly = true)
    public ChallengeStatistics getChallengeStatistics() {
        return ChallengeStatistics.builder()
            .totalChallenges(challengeRepository.count())
            .activeChallenges(countChallengesByStatus(ChallengeStatus.ACTIVE))
            .completedChallenges(countChallengesByStatus(ChallengeStatus.COMPLETED))
            .draftChallenges(countChallengesByStatus(ChallengeStatus.DRAFT))
            .cancelledChallenges(countChallengesByStatus(ChallengeStatus.CANCELLED))
            .expiredChallenges(countChallengesByStatus(ChallengeStatus.EXPIRED))
            .totalActiveParticipants(challengeRepository.getTotalActiveParticipants())
            .averageParticipants(challengeRepository.getAverageParticipantsForCompletedChallenges())
            .averageCompletionRate(challengeRepository.getAverageCompletionRate())
            .build();
    }
    
    // Validation helpers
    
    private void validateCreator(Long creatorId) {
        if (!userRepository.existsById(creatorId)) {
            throw new RuntimeException("El usuario creador no existe");
        }
    }
    
    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("El usuario no existe");
        }
    }
    
    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas");
        }
        
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha de inicio debe ser futura");
        }
    }
    
    // Inner class for statistics
    public static class ChallengeStatistics {
        private long totalChallenges;
        private long activeChallenges;
        private long completedChallenges;
        private long draftChallenges;
        private long cancelledChallenges;
        private long expiredChallenges;
        private Long totalActiveParticipants;
        private Double averageParticipants;
        private Double averageCompletionRate;
        
        public static ChallengeStatisticsBuilder builder() {
            return new ChallengeStatisticsBuilder();
        }
        
        public static class ChallengeStatisticsBuilder {
            private ChallengeStatistics stats = new ChallengeStatistics();
            
            public ChallengeStatisticsBuilder totalChallenges(long totalChallenges) {
                stats.totalChallenges = totalChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder activeChallenges(long activeChallenges) {
                stats.activeChallenges = activeChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder completedChallenges(long completedChallenges) {
                stats.completedChallenges = completedChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder draftChallenges(long draftChallenges) {
                stats.draftChallenges = draftChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder cancelledChallenges(long cancelledChallenges) {
                stats.cancelledChallenges = cancelledChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder expiredChallenges(long expiredChallenges) {
                stats.expiredChallenges = expiredChallenges;
                return this;
            }
            
            public ChallengeStatisticsBuilder totalActiveParticipants(Long totalActiveParticipants) {
                stats.totalActiveParticipants = totalActiveParticipants;
                return this;
            }
            
            public ChallengeStatisticsBuilder averageParticipants(Double averageParticipants) {
                stats.averageParticipants = averageParticipants;
                return this;
            }
            
            public ChallengeStatisticsBuilder averageCompletionRate(Double averageCompletionRate) {
                stats.averageCompletionRate = averageCompletionRate;
                return this;
            }
            
            public ChallengeStatistics build() {
                return stats;
            }
        }
        
        // Getters
        public long getTotalChallenges() { return totalChallenges; }
        public long getActiveChallenges() { return activeChallenges; }
        public long getCompletedChallenges() { return completedChallenges; }
        public long getDraftChallenges() { return draftChallenges; }
        public long getCancelledChallenges() { return cancelledChallenges; }
        public long getExpiredChallenges() { return expiredChallenges; }
        public Long getTotalActiveParticipants() { return totalActiveParticipants; }
        public Double getAverageParticipants() { return averageParticipants; }
        public Double getAverageCompletionRate() { return averageCompletionRate; }
    }
}
