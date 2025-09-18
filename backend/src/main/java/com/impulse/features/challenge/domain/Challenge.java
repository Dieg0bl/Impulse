package com.impulse.features.challenge.domain;

import com.impulse.shared.enums.ChallengeStatus;
import com.impulse.shared.enums.Visibility;
import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity: Challenge
 * Business logic for challenge lifecycle and rules
 * Challenge: DRAFT → OPEN → CLOSED
 * visibility: PRIVATE|PUBLIC|LINK (default PRIVATE)
 */
public class Challenge {
    private final ChallengeId id;
    private final Long ownerUserId;
    private String title;
    private String description;
    private ChallengeStatus status;
    private Visibility visibility;
    private String category;
    private String publicConsentVersion;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Constructor for creation
    public Challenge(ChallengeId id, Long ownerUserId, String title, String description,
                    String category, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "Challenge ID cannot be null");
        this.ownerUserId = Objects.requireNonNull(ownerUserId, "Owner user ID cannot be null");
        setTitle(title);
        setDescription(description);
        this.status = ChallengeStatus.DRAFT;
        this.visibility = Visibility.PRIVATE;
        this.category = category != null ? category : "General";
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = createdAt;
    }

    // Constructor for reconstruction
    public Challenge(ChallengeId id, Long ownerUserId, String title, String description,
                    ChallengeStatus status, Visibility visibility, String category,
                    String publicConsentVersion, LocalDateTime openedAt, LocalDateTime closedAt,
                    LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.visibility = visibility;
        this.category = category;
        this.publicConsentVersion = publicConsentVersion;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Factory method
    public static Challenge create(Long ownerUserId, String title, String description, String category) {
        return new Challenge(
            ChallengeId.generate(),
            ownerUserId,
            title,
            description,
            category,
            LocalDateTime.now()
        );
    }

    // Business methods
    public void open(String consentVersion) {
        if (status != ChallengeStatus.DRAFT) {
            throw new DomainException("Challenge can only be opened from DRAFT status");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new DomainException("Cannot open challenge without title");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new DomainException("Cannot open challenge without description");
        }

        this.status = ChallengeStatus.OPEN;
        this.openedAt = LocalDateTime.now();
        this.publicConsentVersion = consentVersion;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        if (status != ChallengeStatus.OPEN) {
            throw new DomainException("Challenge can only be closed from OPEN status");
        }

        this.status = ChallengeStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void changeVisibility(Visibility newVisibility, String consentVersion) {
        if (newVisibility == null) {
            throw new DomainException("Visibility cannot be null");
        }
        if (this.visibility == newVisibility) {
            return; // No change needed
        }

        // If making public, require consent version
        if (newVisibility == Visibility.PUBLIC &&
            (consentVersion == null || consentVersion.trim().isEmpty())) {
            throw new DomainException("Consent version required when making challenge public");
        }

        this.visibility = newVisibility;
        if (newVisibility == Visibility.PUBLIC) {
            this.publicConsentVersion = consentVersion;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description) {
        if (status == ChallengeStatus.CLOSED) {
            throw new DomainException("Cannot update closed challenge");
        }

        setTitle(title);
        setDescription(description);
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean canBeOpened() {
        return status == ChallengeStatus.DRAFT && !isDeleted();
    }

    public boolean canBeClosed() {
        return status == ChallengeStatus.OPEN && !isDeleted();
    }

    public boolean canBeUpdated() {
        return status != ChallengeStatus.CLOSED && !isDeleted();
    }

    // Private validation methods
    private void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new DomainException("Challenge title is required");
        }
        if (title.length() > 200) {
            throw new DomainException("Challenge title cannot exceed 200 characters");
        }
        this.title = title.trim();
    }

    private void setDescription(String description) {
        if (description != null && description.length() > 65535) {
            throw new DomainException("Challenge description is too long");
        }
        this.description = description;
    }

    // Getters
    public ChallengeId getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChallengeStatus getStatus() { return status; }
    public Visibility getVisibility() { return visibility; }
    public String getCategory() { return category; }
    public String getPublicConsentVersion() { return publicConsentVersion; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(id, challenge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
