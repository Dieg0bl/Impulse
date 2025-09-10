package com.impulse.application.service;

import com.impulse.domain.model.Coach;
import com.impulse.domain.model.User;
import com.impulse.domain.model.enums.CoachStatus;
import com.impulse.domain.model.enums.CoachSpecialty;
import com.impulse.infrastructure.persistence.CoachRepository;
import com.impulse.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de Coaches
 */
@Service
@Transactional
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;
    
    @Autowired
    private UserRepository userRepository;

    // ===== OPERACIONES CRUD =====
    
    /**
     * Crear nuevo coach
     */
    public Coach createCoach(Long userId, CoachSpecialty specialty, String bio, String approachDescription, 
                            Integer yearsOfExperience, Set<String> certifications, Set<String> languages) {
        
        // Verificar que el usuario existe y no es ya coach
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (coachRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("El usuario ya es coach");
        }
        
        Coach coach = new Coach();
        coach.setUser(user);
        coach.setSpecialty(specialty);
        coach.setBio(bio);
        coach.setApproachDescription(approachDescription);
        coach.setYearsOfExperience(yearsOfExperience != null ? yearsOfExperience : 0);
        coach.setCertifications(certifications);
        coach.setLanguages(languages);
        coach.setStatus(CoachStatus.PENDING_APPROVAL);
        coach.setIsAvailableForNewClients(false);
        coach.setIsVerified(false);
        coach.setAverageRating(0.0);
        coach.setTotalSessions(0);
        coach.setMaxClientsPerMonth(10); // Default
        coach.setHourlyRate(50.0); // Default
        
        return coachRepository.save(coach);
    }
    
    /**
     * Obtener coach por ID
     */
    public Optional<Coach> getCoachById(Long id) {
        return coachRepository.findById(id);
    }
    
    /**
     * Obtener coach por usuario
     */
    public Optional<Coach> getCoachByUserId(Long userId) {
        return coachRepository.findByUserId(userId);
    }
    
    /**
     * Actualizar información del coach
     */
    public Coach updateCoach(Long id, String bio, String approachDescription, 
                            Set<String> certifications, Set<String> languages,
                            Integer maxClientsPerMonth, Double hourlyRate) {
        
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        if (bio != null) coach.setBio(bio);
        if (approachDescription != null) coach.setApproachDescription(approachDescription);
        if (certifications != null) coach.setCertifications(certifications);
        if (languages != null) coach.setLanguages(languages);
        if (maxClientsPerMonth != null) coach.setMaxClientsPerMonth(maxClientsPerMonth);
        if (hourlyRate != null) coach.setHourlyRate(hourlyRate);
        
        return coachRepository.save(coach);
    }
    
    /**
     * Eliminar coach
     */
    public void deleteCoach(Long id) {
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        coach.setStatus(CoachStatus.INACTIVE);
        coachRepository.save(coach);
    }
    
    // ===== GESTIÓN DE ESTADO =====
    
    /**
     * Aprobar coach
     */
    public Coach approveCoach(Long id) {
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        if (coach.getStatus() != CoachStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Solo se pueden aprobar coaches pendientes");
        }
        
        coach.setStatus(CoachStatus.ACTIVE);
        coach.setIsAvailableForNewClients(true);
        
        return coachRepository.save(coach);
    }
    
    /**
     * Rechazar coach
     */
    public Coach rejectCoach(Long id, String reason) {
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        coach.setStatus(CoachStatus.REJECTED);
        coach.setIsAvailableForNewClients(false);
        // Aquí se podría guardar la razón del rechazo en un campo adicional
        
        return coachRepository.save(coach);
    }
    
    /**
     * Verificar coach
     */
    public Coach verifyCoach(Long id) {
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        coach.setIsVerified(true);
        return coachRepository.save(coach);
    }
    
    /**
     * Cambiar disponibilidad
     */
    public Coach toggleAvailability(Long id) {
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        coach.setIsAvailableForNewClients(!coach.getIsAvailableForNewClients());
        return coachRepository.save(coach);
    }
    
    // ===== BÚSQUEDAS Y FILTROS =====
    
    /**
     * Obtener todos los coaches disponibles
     */
    @Transactional(readOnly = true)
    public List<Coach> getAvailableCoaches() {
        return coachRepository.findAvailableCoaches();
    }
    
    /**
     * Obtener coaches disponibles paginados
     */
    @Transactional(readOnly = true)
    public Page<Coach> getAvailableCoaches(Pageable pageable) {
        return coachRepository.findAvailableCoaches(pageable);
    }
    
    /**
     * Buscar coaches por especialidad
     */
    @Transactional(readOnly = true)
    public Page<Coach> getCoachesBySpecialty(CoachSpecialty specialty, Pageable pageable) {
        return coachRepository.findAvailableCoachesBySpecialty(specialty, pageable);
    }
    
    /**
     * Buscar coaches por rating mínimo
     */
    @Transactional(readOnly = true)
    public Page<Coach> getCoachesByMinimumRating(Double minRating, Pageable pageable) {
        return coachRepository.findByMinimumRating(minRating, pageable);
    }
    
    /**
     * Obtener top coaches
     */
    @Transactional(readOnly = true)
    public List<Coach> getTopCoaches(int limit) {
        return coachRepository.findTopCoaches(Pageable.ofSize(limit));
    }
    
    /**
     * Buscar coaches compatibles
     */
    @Transactional(readOnly = true)
    public Page<Coach> findCompatibleCoaches(String preferredLanguage, CoachSpecialty specialty, 
                                           Double minRating, Pageable pageable) {
        return coachRepository.findCompatibleCoaches(preferredLanguage, specialty, minRating, pageable);
    }
    
    /**
     * Búsqueda general de coaches
     */
    @Transactional(readOnly = true)
    public Page<Coach> searchCoaches(String query, Pageable pageable) {
        return coachRepository.searchCoaches(query, pageable);
    }
    
    // ===== ESTADÍSTICAS =====
    
    /**
     * Actualizar rating del coach
     */
    public Coach updateRating(Long coachId, Double newRating) {
        Coach coach = coachRepository.findById(coachId)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        // Aquí se implementaría la lógica para calcular el nuevo promedio
        // Por simplicidad, asumimos que se pasa el nuevo promedio calculado
        coach.setAverageRating(newRating);
        
        return coachRepository.save(coach);
    }
    
    /**
     * Incrementar contador de sesiones
     */
    public Coach incrementSessionCount(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
            .orElseThrow(() -> new RuntimeException("Coach no encontrado"));
        
        coach.setTotalSessions(coach.getTotalSessions() + 1);
        return coachRepository.save(coach);
    }
    
    /**
     * Obtener estadísticas globales
     */
    @Transactional(readOnly = true)
    public CoachStatistics getGlobalStatistics() {
        Long totalCoaches = coachRepository.countByStatus(CoachStatus.ACTIVE);
        Long verifiedCoaches = coachRepository.countVerifiedCoaches();
        Double averageRating = coachRepository.getAverageRatingAcrossAllCoaches();
        Double averageExperience = coachRepository.getAverageExperienceAcrossAllCoaches();
        Long totalSessions = coachRepository.getTotalSessionsAcrossAllCoaches();
        
        return new CoachStatistics(totalCoaches, verifiedCoaches, averageRating, 
                                 averageExperience, totalSessions);
    }
    
    /**
     * Obtener distribución por especialidad
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDistributionBySpecialty() {
        return coachRepository.getCoachDistributionBySpecialty();
    }
    
    // ===== VALIDACIONES =====
    
    /**
     * Verificar si un usuario puede ser coach
     */
    public boolean canUserBecomeCoach(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return false;
        }
        
        Optional<Coach> existingCoach = coachRepository.findByUserId(userId);
        return existingCoach.isEmpty();
    }
    
    /**
     * Validar disponibilidad del coach
     */
    public boolean isCoachAvailable(Long coachId) {
        Optional<Coach> coach = coachRepository.findById(coachId);
        return coach.isPresent() && 
               coach.get().getStatus() == CoachStatus.ACTIVE && 
               coach.get().getIsAvailableForNewClients();
    }
    
    // ===== MANTENIMIENTO =====
    
    /**
     * Desactivar coaches inactivos
     */
    public int deactivateInactiveCoaches(int daysInactive) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysInactive);
        return coachRepository.deactivateInactiveCoaches(cutoffDate);
    }
    
    // ===== CLASE INTERNA PARA ESTADÍSTICAS =====
    
    public static class CoachStatistics {
        private Long totalCoaches;
        private Long verifiedCoaches;
        private Double averageRating;
        private Double averageExperience;
        private Long totalSessions;
        
        public CoachStatistics(Long totalCoaches, Long verifiedCoaches, Double averageRating, 
                             Double averageExperience, Long totalSessions) {
            this.totalCoaches = totalCoaches;
            this.verifiedCoaches = verifiedCoaches;
            this.averageRating = averageRating;
            this.averageExperience = averageExperience;
            this.totalSessions = totalSessions;
        }
        
        // Getters
        public Long getTotalCoaches() { return totalCoaches; }
        public Long getVerifiedCoaches() { return verifiedCoaches; }
        public Double getAverageRating() { return averageRating; }
        public Double getAverageExperience() { return averageExperience; }
        public Long getTotalSessions() { return totalSessions; }
    }
}
