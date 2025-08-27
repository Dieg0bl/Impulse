package com.impulse.domain.analytics;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String payload;
    private LocalDateTime createdAt;

    public Event() {}

    public Event(Long id, String name, String payload) {
        this.id = id;
        this.name = name;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
