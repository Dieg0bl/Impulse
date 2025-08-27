package com.impulse.domain.auditoria;

import java.time.LocalDateTime;

/**
 * Minimal AuditRecord domain type used by AuditoriaRepositoryPort.
 * This is a lightweight stub to allow compilation; real fields can be extended.
 */
public class AuditRecord {
    private Long id;
    private String action;
    private LocalDateTime createdAt;

    public AuditRecord() {}

    public AuditRecord(Long id, String action) {
        this.id = id;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
