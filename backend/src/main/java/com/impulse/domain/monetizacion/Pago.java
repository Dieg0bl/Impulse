package com.impulse.domain.monetizacion;

import java.time.LocalDateTime;

import com.impulse.domain.usuario.Usuario;

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
 * Entidad Pago (Monetizacion).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - usuario: público (relación con Usuario)
 * - cantidad: confidencial (requiere consentimiento)
 * - moneda: público
 * - estado: público
 * - metodo: público (STRIPE, PAYPAL, etc.)
 * - referencia: confidencial
 * - createdAt, updatedAt, deletedAt: auditoría
 */
@Entity
@Table(name = "pagos")
public class Pago {
    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario asociado al pago (público, FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Cantidad pagada (confidencial, requiere consentimiento) */
    @Column(nullable = false)
    private Double cantidad;

    /** Moneda (público: EUR, USD, etc.) */
    @Column(nullable = false)
    private String moneda;

    /** Estado del pago (público: PENDIENTE, COMPLETADO, FALLIDO) */
    @Column(nullable = false)
    private String estado;

    /** Método de pago (público: STRIPE, PAYPAL, etc.) */
    @Column(nullable = false)
    private String metodo;

    /** Referencia de la transacción (confidencial) */
    @Column(nullable = true, length = 128)
    private String referencia;

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
    public Pago() {
        // Este constructor está intencionadamente vacío.
        // Es requerido por JPA para la instanciación de la entidad mediante reflexión.
    }

    // Builder pattern para evitar constructores con demasiados parámetros
    public static class Builder {
        private Long id;
        private Usuario usuario;
        private Double cantidad;
        private String moneda;
        private String estado;
        private String metodo;
        private String referencia;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private String createdBy;
        private String updatedBy;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder usuario(Usuario usuario) { this.usuario = usuario; return this; }
        public Builder cantidad(Double cantidad) { this.cantidad = cantidad; return this; }
        public Builder moneda(String moneda) { this.moneda = moneda; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder metodo(String metodo) { this.metodo = metodo; return this; }
        public Builder referencia(String referencia) { this.referencia = referencia; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }

        public Pago build() {
            Pago pago = new Pago();
            pago.id = this.id;
            pago.usuario = this.usuario;
            pago.cantidad = this.cantidad;
            pago.moneda = this.moneda;
            pago.estado = this.estado;
            pago.metodo = this.metodo;
            pago.referencia = this.referencia;
            pago.createdAt = this.createdAt;
            pago.updatedAt = this.updatedAt;
            pago.deletedAt = this.deletedAt;
            pago.createdBy = this.createdBy;
            pago.updatedBy = this.updatedBy;
            return pago;
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
    
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
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
