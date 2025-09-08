package com.impulse.domain.tutor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Tutor.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - email: público (requiere consentimiento explícito)
 * - nombre: público
 * - usuarios: público (relación con Usuario)
 * - createdAt, updatedAt, deletedAt: auditoría
 */
@Entity
@Table(name = "tutores")
public class Tutor {

    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email del tutor (público, UNIQUE, requiere consentimiento) */
    @Column(nullable = false, unique = true)
    private String email;

    /** Nombre del tutor (público) */
    @Column(nullable = false)
    private String nombre;

    /** Relación con usuarios (N:M, público) */
    @ManyToMany(mappedBy = "validadores")
    private List<com.impulse.domain.usuario.Usuario> usuarios;

    /** Fecha de creación (auditoría, no modificable) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización (auditoría) */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Fecha de borrado lógico (auditoría, soft delete) */
    @Column(nullable = true)
    private LocalDateTime deletedAt;

    /** Usuario que creó el registro (auditoría) */
    @Column(nullable = false)
    private String createdBy;

    /** Usuario que actualizó el registro (auditoría) */
    @Column(nullable = false)
    private String updatedBy;

    // Getters y setters omitidos por brevedad. Implementar según estándar JavaBean.
    // --- Getters y setters obligatorios para mappers, DTOs y JPA ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public java.util.List<com.impulse.domain.usuario.Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(java.util.List<com.impulse.domain.usuario.Usuario> usuarios) { this.usuarios = usuarios; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public java.time.LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(java.time.LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
