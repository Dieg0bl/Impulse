package com.impulse.domain.usuario;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Usuario.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Solo datos, sin lógica de negocio.
 */
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String password;
    private LocalDateTime fechaNacimiento;
    private String estado;
    private String roles;
    private boolean consentimientoAceptado;

    public UsuarioDTO() {
        // Constructor necesario para frameworks y serialización
    }

    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDateTime fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public boolean isConsentimientoAceptado() { return consentimientoAceptado; }
    public void setConsentimientoAceptado(boolean consentimientoAceptado) { this.consentimientoAceptado = consentimientoAceptado; }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioDTO that = (UsuarioDTO) o;
        return consentimientoAceptado == that.consentimientoAceptado &&
                Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(nombre, that.nombre) &&
                Objects.equals(password, that.password) &&
                Objects.equals(fechaNacimiento, that.fechaNacimiento) &&
                Objects.equals(estado, that.estado) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, nombre, password, fechaNacimiento, estado, roles, consentimientoAceptado);
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado='" + estado + '\'' +
                ", roles='" + roles + '\'' +
                ", consentimientoAceptado=" + consentimientoAceptado +
                '}';
    }
}
