package com.impulse.domain.reto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para Reto. Cumple compliance: RGPD, ISO 27001, ENS.
 * No expone datos sensibles ni relaciones completas.
 */
public class RetoDTO {
private java.time.LocalDateTime fechaLimite;
private Long id;
private String titulo;
private String descripcion;
private LocalDateTime fechaInicio;
private LocalDateTime fechaFin;
private String estado;
private Long usuarioId;
private List<Long> tutoresIds;
private List<Long> evidenciasIds;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private LocalDateTime deletedAt;
private String createdBy;
private String updatedBy;
private String deletedBy;

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getTitulo() { return titulo; }
public void setTitulo(String titulo) { this.titulo = titulo; }
public String getDescripcion() { return descripcion; }
public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
public LocalDateTime getFechaInicio() { return fechaInicio; }
public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
public java.time.LocalDateTime getFechaLimite() { return fechaLimite; }
public void setFechaLimite(java.time.LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
public LocalDateTime getFechaFin() { return fechaFin; }
public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
public String getEstado() { return estado; }
public void setEstado(String estado) { this.estado = estado; }
public Long getUsuarioId() { return usuarioId; }
public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
public List<Long> getTutoresIds() { return tutoresIds; }
public void setTutoresIds(List<Long> tutoresIds) { this.tutoresIds = tutoresIds; }
public List<Long> getEvidenciasIds() { return evidenciasIds; }
public void setEvidenciasIds(List<Long> evidenciasIds) { this.evidenciasIds = evidenciasIds; }
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
}
