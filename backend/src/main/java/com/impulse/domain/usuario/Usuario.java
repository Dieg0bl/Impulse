package com.impulse.domain.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Usuario.
 * Cumple con las directrices de compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - email: público (requiere consentimiento explícito)
 * - password: secreto (cifrado, nunca expuesto)
 * - nombre: público
 * - fechaNacimiento: confidencial (requiere consentimiento)
 * - validadores: público (relación con Tutor)
 * - createdAt, updatedAt, deletedAt: auditoría
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    /** Consentimiento RGPD aceptado (obligatorio para operar, compliance) */
    @Column(nullable = false)
    private boolean consentimientoAceptado;

    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email del usuario (público, UNIQUE, requiere consentimiento) */
    @Column(nullable = false, unique = true)
    private String email;

    /** Contraseña cifrada (secreto, nunca exponer, requiere consentimiento) */
    @Column(nullable = false)
    private String password;

    /** Nombre del usuario (público) */
    @Column(nullable = false)
    private String nombre;

    /** Fecha de nacimiento (confidencial, requiere consentimiento) */
    @Column(nullable = true)
    private LocalDateTime fechaNacimiento;

    /** Estado del usuario (público: PENDIENTE, ACTIVO, BLOQUEADO, ELIMINADO) */
    @Column(nullable = false)
    private String estado;

    /** Roles del usuario (público, ej: USER, ADMIN) */
    @Column(nullable = false, length = 50)
    private String roles;

    /**
     * Relación con Tutor (validadores humanos, público).
     * Un usuario puede tener varios validadores (N:M).
     * Justificación: presión social real y validación humana.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_tutor",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id")
    )
    private List<com.impulse.domain.tutor.Tutor> validadores;

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

    /** Usuario que borró el registro (auditoría, confidencial, puede ser null) */
    @Column
    private String deletedBy;

    // Getters y setters omitidos por brevedad. Implementar según estándar JavaBean.
    // --- Getters y setters obligatorios para mappers, DTOs y JPA ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDateTime getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDateTime fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public List<com.impulse.domain.tutor.Tutor> getValidadores() { return validadores; }
    public void setValidadores(List<com.impulse.domain.tutor.Tutor> validadores) { this.validadores = validadores; }
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
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }

    public boolean isConsentimientoAceptado() { return consentimientoAceptado; }
    public void setConsentimientoAceptado(boolean consentimientoAceptado) { this.consentimientoAceptado = consentimientoAceptado; }
}
