package com.impulse.domain.pmf;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(nullable = false, length = 100)
    private String eventType;
    @Column(columnDefinition = "json")
    private String properties;
    @Column(nullable = false)
    private Instant timestamp = Instant.now();
    private String sessionId;
    private String source;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getProperties() { return properties; }
    public void setProperties(String properties) { this.properties = properties; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
