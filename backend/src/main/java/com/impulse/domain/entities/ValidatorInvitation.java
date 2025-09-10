package com.impulse.domain.entities;

import com.impulse.domain.enums.InvitationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Invitaciones para ser validador de un reto
 * - Sistema de invitaciones por usuario
 * - Control de expiración
 * - Estados de la invitación
 */
@Entity
@Table(name = "validator_invitations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"challenge_id", "invitee_id"}),
       indexes = {
           @Index(name = "idx_invitations_challenge_id", columnList = "challenge_id"),
           @Index(name = "idx_invitations_invitee_id", columnList = "invitee_id"),
           @Index(name = "idx_invitations_status", columnList = "status"),
           @Index(name = "idx_invitations_expires_at", columnList = "expires_at")
       })
public class ValidatorInvitation {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    @NotNull(message = "Challenge is required")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    @NotNull(message = "Inviter is required")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    @NotNull(message = "Invitee is required")
    private User invitee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "message", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    @CreationTimestamp
    @Column(name = "invited_at", nullable = false, updatable = false)
    private LocalDateTime invitedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "expires_at", nullable = false)
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;

    // === CONSTRUCTORS ===

    public ValidatorInvitation() {}

    public ValidatorInvitation(Challenge challenge, User inviter, User invitee, String message) {
        this.challenge = challenge;
        this.inviter = inviter;
        this.invitee = invitee;
        this.message = message;
        this.status = InvitationStatus.PENDING;
        this.expiresAt = LocalDateTime.now().plusDays(7); // 7 días para responder
    }

    // === BUSINESS METHODS ===

    /**
     * Acepta la invitación
     */
    public void accept() {
        validateCanRespond();
        this.status = InvitationStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * Rechaza la invitación
     */
    public void decline() {
        validateCanRespond();
        this.status = InvitationStatus.DECLINED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * Marca como expirada
     */
    public void expire() {
        if (this.status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.EXPIRED;
            this.respondedAt = LocalDateTime.now();
        }
    }

    /**
     * Verifica si puede responder
     */
    private void validateCanRespond() {
        if (this.status != InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation has already been responded to");
        }
        
        if (isExpired()) {
            throw new IllegalStateException("Invitation has expired");
        }
    }

    /**
     * Verifica si ha expirado
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Verifica si está pendiente y no ha expirado
     */
    public boolean isPendingAndValid() {
        return status == InvitationStatus.PENDING && !isExpired();
    }

    /**
     * Verifica si puede ser aceptada
     */
    public boolean canBeAccepted() {
        return isPendingAndValid() && challenge.isActive();
    }

    /**
     * Obtiene días restantes para responder
     */
    public long getDaysUntilExpiration() {
        if (isExpired()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), expiresAt);
    }

    // === GETTERS AND SETTERS ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }

    public User getInviter() { return inviter; }
    public void setInviter(User inviter) { this.inviter = inviter; }

    public User getInvitee() { return invitee; }
    public void setInvitee(User invitee) { this.invitee = invitee; }

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getInvitedAt() { return invitedAt; }
    public void setInvitedAt(LocalDateTime invitedAt) { this.invitedAt = invitedAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    // === EQUALS, HASHCODE, TOSTRING ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidatorInvitation that = (ValidatorInvitation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ValidatorInvitation{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", invitedAt=" + invitedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
