package com.impulse.domain.notificacion;

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
 * Entidad Notificacion.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Clasificación de campos:
 * - tipo: público
 * - mensaje: público
 * - usuario: público (relación con Usuario)
 * - canal: público (EMAIL, WHATSAPP, TELEGRAM)
 * - enviado: público
 * - createdAt, updatedAt, deletedAt: auditoría
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {
    /** Identificador único (público) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tipo de notificación (público: EVENTO, ALERTA, RECORDATORIO) */
    @Column(nullable = false)
    private String tipo;

    /** Mensaje de la notificación (público) */
    @Column(nullable = false, length = 1024)
    private String mensaje;

    /** Usuario destinatario (público, FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Canal de envío (público: EMAIL, WHATSAPP, TELEGRAM) */
    @Column(nullable = false)
    private String canal;

    /** Estado de envío (público: ENVIADO, PENDIENTE, ERROR) */
    @Column(nullable = false)
    private String enviado;

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
    public Notificacion() {
        // Este constructor está intencionadamente vacío para cumplir con los requisitos de JPA.
    }

    // Builder pattern para creación de Notificacion
    public static class Builder {
        private Long id;
        private String tipo;
        private String mensaje;
        private Usuario usuario;
        private String canal;
        private String enviado;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private String createdBy;
        private String updatedBy;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tipo(String tipo) { this.tipo = tipo; return this; }
        public Builder mensaje(String mensaje) { this.mensaje = mensaje; return this; }
        public Builder usuario(Usuario usuario) { this.usuario = usuario; return this; }
        public Builder canal(String canal) { this.canal = canal; return this; }
        public Builder enviado(String enviado) { this.enviado = enviado; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }

        public Notificacion build() {
            Notificacion n = new Notificacion();
            n.id = this.id;
            n.tipo = this.tipo;
            n.mensaje = this.mensaje;
            n.usuario = this.usuario;
            n.canal = this.canal;
            n.enviado = this.enviado;
            n.createdAt = this.createdAt;
            n.updatedAt = this.updatedAt;
            n.deletedAt = this.deletedAt;
            n.createdBy = this.createdBy;
            n.updatedBy = this.updatedBy;
            return n;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public String getEnviado() { return enviado; }
    public void setEnviado(String enviado) { this.enviado = enviado; }
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
