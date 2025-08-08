package com.impulse.domain.auditoria;

import java.time.LocalDateTime;

/**
 * DTO para Auditoria. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class AuditoriaDTO {
    private Long id;
    private String accion;
    private String usuario;
    private String resultado;
    private String detalle;
    private LocalDateTime createdAt;

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
}
