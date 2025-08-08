package com.impulse.domain.auditoria;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Auditoria.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - accion: público
 * - usuario: público
 * - resultado: público
 * - detalle: confidencial
 * - createdAt: auditoría
 */
@Entity
@Table(name = "auditorias")
public class Auditoria {
    /** Entidad auditada (público) */
    @Column(nullable = true)
    private String entidad;

    /** ID de la entidad auditada (público) */
    @Column(nullable = true)
    private Long entidadId;

    /** Descripción de la acción (confidencial) */
    @Column(nullable = true, length = 1024)
    private String descripcion;
    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Acción auditada (público) */
    @Column(nullable = false)
    private String accion;

    /** Usuario/actor de la acción (público) */
    @Column(nullable = false)
    private String usuario;

    /** Resultado de la acción (público: EXITO, ERROR) */
    @Column(nullable = false)
    private String resultado;

    /** Detalle adicional (confidencial) */
    @Column(nullable = true, length = 1024)
    private String detalle;

    /** Fecha de creación (auditoría, no modificable) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor vacío requerido por JPA
    public Auditoria() {}

    // Constructor completo para uso en tests/servicios
    public Auditoria(Long id, String accion, String usuario, String resultado, String detalle, LocalDateTime createdAt) {
        this.id = id;
        this.accion = accion;
        this.usuario = usuario;
        this.resultado = resultado;
        this.detalle = detalle;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
