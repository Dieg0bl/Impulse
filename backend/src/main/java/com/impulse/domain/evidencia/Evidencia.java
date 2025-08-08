package com.impulse.domain.evidencia;

import java.time.LocalDateTime;

import com.impulse.domain.reto.Reto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad Evidencia.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - tipo: público
 * - url: confidencial (requiere consentimiento)
 * - descripcion: público
 * - reto: público (relación con Reto)
 * - validado: público
 * - createdAt, updatedAt, deletedAt: auditoría
 */
@Entity
@Table(name = "evidencias")
public class Evidencia {
    /** Comentario del validador/usuario (público, opcional, compliance) */
    @Column(nullable = true, length = 1024)
    private String comentario;
    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tipo de evidencia (público: TEXTO, IMAGEN, VIDEO, AUDIO, LINK) */
    @Column(nullable = false)
    private String tipo;

    /** URL o path de la evidencia (confidencial, requiere consentimiento) */
    @Column(nullable = false, length = 2048)
    private String url;

    /** Descripción de la evidencia (público) */
    @Column(nullable = true, length = 1024)
    private String descripcion;

    /** Reto asociado (público, FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reto_id", nullable = false)
    private Reto reto;

    /** Estado de validación (público: PENDIENTE, VALIDADO, RECHAZADO) */
    @Column(nullable = false)
    private String validado;

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

    // Constructor vacío requerido por JPA
    public Evidencia() {}

    // Constructor completo para uso en tests/servicios
    public Evidencia(Long id, String tipo, String url, String descripcion, Reto reto, String validado, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.tipo = tipo;
        this.url = url;
        this.descripcion = descripcion;
        this.reto = reto;
        this.validado = validado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getter y setter para comentario (compliance)
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Reto getReto() { return reto; }
    public void setReto(Reto reto) { this.reto = reto; }
    public String getValidado() { return validado; }
    public void setValidado(String validado) { this.validado = validado; }
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
