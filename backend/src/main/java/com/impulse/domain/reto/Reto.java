package com.impulse.domain.reto;

import java.time.LocalDateTime;
import java.util.List;

import com.impulse.domain.evidencia.Evidencia;
import com.impulse.domain.tutor.Tutor;
import com.impulse.domain.usuario.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad Reto.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - titulo: público
 * - descripcion: público
 * - fechaInicio, fechaFin: confidencial (requiere consentimiento)
 * - usuario: público (relación con Usuario)
 * - tutores: público (relación con Tutor)
 * - evidencias: confidencial (requiere consentimiento, relación con Evidencia)
 * - estado: público
 * - createdAt, updatedAt, deletedAt: auditoría
 * - createdBy, updatedBy, deletedBy: auditoría
 */
@Entity
@Table(name = "retos")
public class Reto {
    /** Fecha límite del reto (confidencial, compliance, puede ser igual a fechaFin o campo explícito) */
    @Column(nullable = true)
    private LocalDateTime fechaLimite;
    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título del reto (público) */
    @NotNull
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String titulo;

    /** Descripción del reto (público) */
    @NotNull
    @Size(min = 5, max = 1024)
    @Column(nullable = false, length = 1024)
    private String descripcion;

    /** Fecha de inicio (confidencial, requiere consentimiento) */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    /** Fecha de fin (confidencial, requiere consentimiento) */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaFin;

    /** Estado del reto (público: ACTIVO, COMPLETADO, FALLIDO, CANCELADO) */
    @NotNull
    @Column(nullable = false)
    private String estado;

    /** Usuario propietario del reto (público, FK) */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Tutores/validadores asignados (público, N:M) */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reto_tutor",
        joinColumns = @JoinColumn(name = "reto_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id")
    )
    private List<Tutor> tutores;

    /** Evidencias asociadas al reto (confidencial, 1:N, requiere consentimiento) */
    @OneToMany(mappedBy = "reto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Evidencia> evidencias;

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

    /** Usuario que borró el registro (auditoría, nullable, soft delete) */
    @Column(nullable = true)
    private String deletedBy;

    // Constructor vacío requerido por JPA
    public Reto() {}

    // Constructor completo para uso en tests/servicios
    public Reto(Long id, String titulo, String descripcion, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, Usuario usuario, List<Tutor> tutores, List<Evidencia> evidencias, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String createdBy, String updatedBy, String deletedBy) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.usuario = usuario;
        this.tutores = tutores;
        this.evidencias = evidencias;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    // Getter y setter para fechaLimite (compliance)
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<Tutor> getTutores() { return tutores; }
    public void setTutores(List<Tutor> tutores) { this.tutores = tutores; }
    public List<Evidencia> getEvidencias() { return evidencias; }
    public void setEvidencias(List<Evidencia> evidencias) { this.evidencias = evidencias; }
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
    // Cumple ciclo de vida, clasificación y compliance según manual IMPULSE.
}
