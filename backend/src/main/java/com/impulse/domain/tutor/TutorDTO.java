
package com.impulse.domain.tutor;

/**
 * DTO para Tutor.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Solo expone información pública y de auditoría, nunca datos sensibles.
 */
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para Tutor.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Solo expone información pública y de auditoría, nunca datos sensibles.
 * No incluye la lista completa de usuarios, solo sus IDs para evitar fuga de datos.
 */
public class TutorDTO {
    /** Identificador único (público) */
    private Long id;
    /** Email del tutor (público) */
    private String email;
    /** Nombre del tutor (público) */
    private String nombre;
    /** IDs de usuarios validados (público, N:M) */
    private List<Long> usuariosIds;
    /** Fecha de creación (auditoría) */
    private LocalDateTime createdAt;
    /** Fecha de última actualización (auditoría) */
    private LocalDateTime updatedAt;
    /** Fecha de borrado lógico (auditoría, puede ser null) */
    private LocalDateTime deletedAt;
    /** Usuario que creó el registro (auditoría) */
    private String createdBy;
    /** Usuario que actualizó el registro (auditoría) */
    private String updatedBy;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Long> getUsuariosIds() { return usuariosIds; }
    public void setUsuariosIds(List<Long> usuariosIds) { this.usuariosIds = usuariosIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
