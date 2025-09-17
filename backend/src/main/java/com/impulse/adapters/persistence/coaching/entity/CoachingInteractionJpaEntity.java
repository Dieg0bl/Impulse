package com.impulse.adapters.persistence.coaching.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coaching_interactions")
public class CoachingInteractionJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "coach_id")
    private String coachId;

    @Column(name = "interaction_metadata", columnDefinition = "JSON")
    private String interactionMetadata;

    // Constructors
    public CoachingInteractionJpaEntity() {}

    public CoachingInteractionJpaEntity(UUID id, String sessionId, String type, String content,
                                       LocalDateTime timestamp, String userId, String coachId,
                                       String interactionMetadata) {
        this.id = id;
        this.sessionId = sessionId;
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
        this.coachId = coachId;
        this.interactionMetadata = interactionMetadata;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCoachId() { return coachId; }
    public void setCoachId(String coachId) { this.coachId = coachId; }

    public String getInteractionMetadata() { return interactionMetadata; }
    public void setInteractionMetadata(String interactionMetadata) { this.interactionMetadata = interactionMetadata; }
}
