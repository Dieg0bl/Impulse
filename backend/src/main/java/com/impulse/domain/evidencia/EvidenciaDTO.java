package com.impulse.domain.evidencia;

import java.time.LocalDateTime;

/**
 * DTO para Evidencia. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class EvidenciaDTO {
    private String comentario;
    private Long id;
    private String tipo;
    private String url;
    private String descripcion;
    private Long retoId;
    private String validado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Long getRetoId() { return retoId; }
    public void setRetoId(Long retoId) { this.retoId = retoId; }
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
