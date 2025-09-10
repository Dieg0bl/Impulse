package com.impulse.domain.statemachine;

import com.impulse.domain.entities.Challenge;
import com.impulse.domain.enums.ChallengeStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Set;

/**
 * Máquina de Estados para Challenge
 * 
 * Estados: DRAFT -> ACTIVE -> COMPLETED/CANCELLED/EXPIRED
 * 
 * Transiciones válidas:
 * - DRAFT -> ACTIVE (publish)
 * - DRAFT -> CANCELLED (cancel)
 * - ACTIVE -> COMPLETED (complete)
 * - ACTIVE -> CANCELLED (cancel)
 * - ACTIVE -> EXPIRED (auto por fecha)
 */
@Component
public class ChallengeStateMachine {
    
    // Transiciones válidas desde cada estado
    private final EnumMap<ChallengeStatus, Set<ChallengeStatus>> validTransitions;
    
    public ChallengeStateMachine() {
        validTransitions = new EnumMap<>(ChallengeStatus.class);
        initializeTransitions();
    }
    
    /**
     * Inicializa las transiciones válidas
     */
    private void initializeTransitions() {
        validTransitions.put(ChallengeStatus.DRAFT, Set.of(
            ChallengeStatus.ACTIVE,
            ChallengeStatus.CANCELLED
        ));
        
        validTransitions.put(ChallengeStatus.ACTIVE, Set.of(
            ChallengeStatus.COMPLETED,
            ChallengeStatus.CANCELLED,
            ChallengeStatus.EXPIRED
        ));
        
        // Estados finales no pueden transicionar
        validTransitions.put(ChallengeStatus.COMPLETED, Set.of());
        validTransitions.put(ChallengeStatus.CANCELLED, Set.of());
        validTransitions.put(ChallengeStatus.EXPIRED, Set.of());
    }
    
    /**
     * Publica el reto (DRAFT -> ACTIVE)
     */
    public void publish(Challenge challenge) {
        validateTransition(challenge.getStatus(), ChallengeStatus.ACTIVE);
        
        // Validaciones de negocio
        if (challenge.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot publish challenge with start date in the past");
        }
        
        if (challenge.getEndDate().isBefore(challenge.getStartDate())) {
            throw new IllegalStateException("End date cannot be before start date");
        }
        
        if (challenge.getTitle() == null || challenge.getTitle().trim().isEmpty()) {
            throw new IllegalStateException("Challenge must have a title");
        }
        
        if (challenge.getDescription() == null || challenge.getDescription().trim().isEmpty()) {
            throw new IllegalStateException("Challenge must have a description");
        }
        
        challenge.setStatus(ChallengeStatus.ACTIVE);
        challenge.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Completa el reto (ACTIVE -> COMPLETED)
     */
    public void complete(Challenge challenge) {
        validateTransition(challenge.getStatus(), ChallengeStatus.COMPLETED);
        
        // Validaciones de negocio
        if (challenge.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot complete challenge before end date");
        }
        
        challenge.setStatus(ChallengeStatus.COMPLETED);
        challenge.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Cancela el reto (DRAFT/ACTIVE -> CANCELLED)
     */
    public void cancel(Challenge challenge, String reason) {
        validateTransition(challenge.getStatus(), ChallengeStatus.CANCELLED);
        
        // Validaciones de negocio
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
        
        challenge.setStatus(ChallengeStatus.CANCELLED);
        challenge.setUpdatedAt(LocalDateTime.now());
        
        // En una implementación completa, guardaríamos el reason en un campo
        // Por ahora solo validamos que se proporcione
    }
    
    /**
     * Expira el reto automáticamente (ACTIVE -> EXPIRED)
     */
    public void expire(Challenge challenge) {
        validateTransition(challenge.getStatus(), ChallengeStatus.EXPIRED);
        
        // Validaciones de negocio
        if (challenge.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot expire challenge before end date");
        }
        
        challenge.setStatus(ChallengeStatus.EXPIRED);
        challenge.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Reactiva un reto draft (solo para correcciones)
     */
    public void reactivate(Challenge challenge) {
        if (challenge.getStatus() != ChallengeStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled challenges can be reactivated");
        }
        
        // Solo permitir reactivar si no ha empezado
        if (challenge.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot reactivate challenge that has already started");
        }
        
        challenge.setStatus(ChallengeStatus.DRAFT);
        challenge.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Valida si una transición es permitida
     */
    private void validateTransition(ChallengeStatus from, ChallengeStatus to) {
        Set<ChallengeStatus> allowedTransitions = validTransitions.get(from);
        
        if (allowedTransitions == null || !allowedTransitions.contains(to)) {
            throw new IllegalStateException(
                String.format("Invalid transition from %s to %s", from, to)
            );
        }
    }
    
    /**
     * Verifica si una transición es válida
     */
    public boolean canTransition(ChallengeStatus from, ChallengeStatus to) {
        Set<ChallengeStatus> allowedTransitions = validTransitions.get(from);
        return allowedTransitions != null && allowedTransitions.contains(to);
    }
    
    /**
     * Obtiene transiciones válidas desde un estado
     */
    public Set<ChallengeStatus> getValidTransitions(ChallengeStatus from) {
        return validTransitions.getOrDefault(from, Set.of());
    }
    
    /**
     * Verifica si el reto puede ser editado
     */
    public boolean canEdit(Challenge challenge) {
        // Solo los drafts pueden ser editados libremente
        if (challenge.getStatus() == ChallengeStatus.DRAFT) {
            return true;
        }
        
        // Los retos activos pueden tener ediciones limitadas
        if (challenge.getStatus() == ChallengeStatus.ACTIVE) {
            // Solo si no ha empezado aún
            return challenge.getStartDate().isAfter(LocalDate.now());
        }
        
        return false;
    }
    
    /**
     * Verifica si el reto puede recibir evidencias
     */
    public boolean canReceiveEvidences(Challenge challenge) {
        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        
        // Debe estar en el período activo
        if (now.isBefore(challenge.getStartDate()) || now.isAfter(challenge.getEndDate())) {
            return false;
        }
        
        // Si permite envíos tardíos, extender la ventana
        if (challenge.getAllowLateSubmission()) {
            return now.isBefore(challenge.getEndDate().plusDays(7));
        }
        
        return true;
    }
    
    /**
     * Procesa expiración automática de retos
     */
    public void processAutoExpiration(Challenge challenge) {
        // Solo procesar retos activos
        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            return;
        }
        
        LocalDate now = LocalDate.now();
        
        // Si pasó la fecha de fin y no permite envíos tardíos
        if (now.isAfter(challenge.getEndDate()) && !challenge.getAllowLateSubmission()) {
            expire(challenge);
            return;
        }
        
        // Si permite envíos tardíos, expirar después del período de gracia
        if (challenge.getAllowLateSubmission() && 
            now.isAfter(challenge.getEndDate().plusDays(7))) {
            expire(challenge);
        }
    }
}
